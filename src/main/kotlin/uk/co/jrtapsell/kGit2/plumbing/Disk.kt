package uk.co.jrtapsell.kGit2.plumbing

import uk.co.jrtapsell.kGit2.Hash
import uk.co.jrtapsell.kGit2.Repo
import uk.co.jrtapsell.kGit2.extensions.*
import java.io.*
import java.nio.channels.Channels
import java.util.*
import java.util.zip.InflaterInputStream

data class GObject(val content: InputStream, val type: ObjectType): Closeable {
    override fun close() {
        content.close()
    }
}

object Disk {
    object cache {
        private val indexCache = WeakHashMap<Repo, Map<Hash, PackLocation>>()

        operator fun get(repo: Repo): Map<Hash, PackLocation> {
            val cached = indexCache.get(repo)
            if (cached != null) return cached

            val indices = repo.gitFolder.resolve("objects")
                .resolve("pack")
                .listFiles()
                .filter { it.extension == "idx" }
            val data = indices
                .flatMap {
                    parseIndex(it).map { (hash, offset) -> hash to PackLocation(it.resolveSibling(it.nameWithoutExtension + ".pack"), offset) } }
                .associate { it }
            indexCache.put(repo, data)
            return data
        }
    }

    fun getObjectByHash(repo: Repo, hash: Hash): GObject? {
        val unpackedFile = repo.gitFolder
            .resolve("objects")
            .resolve(hash.asciiPrefix(1))
            .resolve(hash.asciiSuffix(19))

        if (unpackedFile.exists()) {
            val inflaterInputStream = InflaterInputStream(unpackedFile.inputStream())
            val sb = StringBuffer()

            var r = inflaterInputStream.read()
            while (r != 0) {
                sb.appendCodePoint(r)
                r = inflaterInputStream.read()
            }

            val name = sb.toString().split(" ")[0]

            val type = ObjectType.getByTextual(name)!!

            return GObject(inflaterInputStream, type)
        }
        val index = cache[repo]
        val location = index.get(hash) ?: return null

        return getFromPack(location, repo)
    }

    fun getFromPack(location: PackLocation, repo: Repo): GObject {
        val rf = RandomAccessFile(location.file, "r")
        rf.seek(location.offset)
        val inputStream = Channels.newInputStream(rf.channel)

        val (first, length) = inputStream.readGitVaryBig(4)
        val typeIndex = (first and 0b01110000) shr 4
        val type = ObjectType.values()[typeIndex]
        return when {
            !type.isDelta -> GObject(InflaterInputStream(inputStream), type)
            type == ObjectType.REF_DELTA -> readDeltaReference(inputStream, location, repo, length)
            type == ObjectType.OFS_DELTA -> readDeltaOffset(inputStream, location, repo, rf, length)
            else -> TODO("$typeIndex ${type.name}")
        }
    }

    fun readDeltaReference(inputStream: InputStream, location: PackLocation, repo: Repo, length: Long): GObject {
        val buffer = ByteArray(20)
        inputStream.fillOrFail(buffer)
        val target = Hash(buffer)
        // https://github.com/git/git/blob/master/packfile.c#L1043
        val original = getFromPack(cache[repo].get(target)!!, repo)
        val data = InflaterInputStream(inputStream)
        val stream = undelta(original.content, data, length)
        return GObject(stream, original.type)
    }

    fun readDeltaOffset(
        inputStream: InputStream,
        location: PackLocation,
        repo: Repo,
        randomAccessFile: RandomAccessFile,
        length: Long): GObject {

        var used = 0
        /*
        var c = inputStream.read()
        var base = c and 127
        while (c and 128 != 0) {
            base += 1
            c = inputStream.read()
            base = (base shl 7) + (c and 127)
        }
        */
        val (_,base,_) = inputStream.readDeltaValue()
        val offset = location.offset - base
        val original = getFromPack(PackLocation(location.file, offset), repo)
        val stream = undelta(original.content, InflaterInputStream(inputStream), length)
        return GObject(stream, original.type)
    }

    fun undelta(original: InputStream, deltasInRaw: InputStream, length: Long): InputStream {
        val bytes = deltasInRaw.readAllBytes()
        val deltasIn = ByteArrayInputStream(bytes)
        val (_, inputSize, inputCount) = deltasIn.readLittleVary() // Source Size
        val (_, resultSize, resultLength) = deltasIn.readLittleVary() // Delta Size

        val returnData = ByteArray(resultSize.toInt())
        var retOffset = 0

        val originalData = original.readAllBytes()
        val deltasBytes = deltasIn.readAllBytes()



        val deltas = ByteArrayInputStream(deltasBytes)

        while (retOffset < returnData.size) {
            val commandByte = deltas.read()
            if (commandByte == -1) break
            if (commandByte == 0) throw AssertionError()
            when (commandByte shr 7) {
                0 -> {
                    val items = commandByte and 127
                    deltas.read(returnData, retOffset, items)
                    retOffset += items
                }
                1 -> {
                    var copyOffset = 0
                    if (commandByte and 0b00000001 != 0) copyOffset += deltas.read()
                    if (commandByte and 0b00000010 != 0) copyOffset += deltas.read() shl 8
                    if (commandByte and 0b00000100 != 0) copyOffset += deltas.read() shl 16
                    if (commandByte and 0b00001000 != 0) copyOffset += deltas.read() shl 24
                    var copySize = 0
                    if (commandByte and 0b00010000 != 0) copySize += deltas.read()
                    if (commandByte and 0b00100000 != 0) copySize += deltas.read() shl 8
                    if (commandByte and 0b01000000 != 0) copySize += deltas.read() shl 16

                    if (copySize == 0)
                        copySize = 0x10000

                    System.arraycopy(originalData, copyOffset, returnData, retOffset, copySize)
                    retOffset += copySize
                }
            }
        }

        return ByteArrayInputStream(returnData)
    }

    data class PackLocation(val file: File, val offset: Long)

    fun parseIndex(it: File): List<Pair<Hash, Long>> {
        val r = it.inputStream().use { input ->
            val buffer4 = ByteArray(4)

            arrayOf(255, 116, 79, 99).forEach { expected ->
                if (input.read() != expected) throw AssertionError("Bad header")
            }

            input.fillOrFail(buffer4)
            if (buffer4.normalise().toLong() != 2L) throw AssertionError("Bad version")

            (0..255).forEach { input.fillOrFail(buffer4) }

            val totalAmount = buffer4.normalise().toLong()

            val hashes = (0 until totalAmount).map {
                val data = ByteArray(20)
                input.fillOrFail(data)
                Hash(data)
            }
            (0 until totalAmount).forEach { input.fillOrFail(buffer4) }
            val offsets = (0 until totalAmount).map {
                input.fillOrFail(buffer4)
                val ret = buffer4.normalise().toLong()
                if ((ret shr 31) == 1L) TODO()
                ret
            }
            hashes.zip(offsets)
        }
        return r
    }

    fun allHashes(repo: Repo): List<Hash> {
        val plain = repo.gitFolder.resolve("objects").listFiles().flatMap { prefixFile ->
            prefixFile.list().filter { it.length == 40 }.map { prefixFile.name + it }
        }.map { Hash(it) }

        val indexes = cache[repo].keys
        return (plain + indexes).sortedBy { it.text() }
    }
}