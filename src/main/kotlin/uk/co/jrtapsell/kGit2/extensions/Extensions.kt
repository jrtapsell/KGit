package uk.co.jrtapsell.kGit2.extensions

import java.io.InputStream

fun Byte.unpack() = toInt() and 0xFF

fun Int.toHexByte() = toString(16).padStart(2, '0')
fun Int.pack() = toByte()

fun Sequence<Byte>.toHexString() = this
    .map(Byte::unpack)
    .joinToString("", transform = Int::toHexByte)

fun InputStream.fillOrFail(buffer: ByteArray) {
    val count = readNBytes(buffer, 0, buffer.size)
    if (count != buffer.size) {
        throw AssertionError("Not enough data")
    }
}

fun ByteArray.normalise() = asSequence().map { it.toInt() and 0xFF }
fun Sequence<Int>.toLong() = fold(0L) {acc, value -> (acc * 256) + value}

fun InputStream.readGitVaryBig(firstSkip: Int): Pair<Int, Long> {
    val first = read()
    val cleanedFirst = first % (1 shl (8 - firstSkip))
    if (first < 128) return first to cleanedFirst.toLong()

    val others = generateSequence (read()) { r ->
        if (r >= 128) read() else null
    }.toList()

    val main = others.asReversed().fold(0L) { acc, it ->
        val value = it and 0b01111111
        (acc * 128) + value
    }

    val result = (main shl (8-firstSkip)) + cleanedFirst
    return first to result
}

// From JGit
fun InputStream.readDeltaValue(): Triple<Int, Long, Int> {
    val i = read()
    var count = 1
    var c = i
    var base = (c and 127).toLong()
    while (c and 128 != 0) {
        base += 1
        c = read()
        count++
        base = base shl 7
        base += (c and 127).toLong()
    }
    return Triple(i, base, count)
}

fun InputStream.readLittleVary(): Triple<Int, Long, Int> {
    val items = mutableListOf<Int>()
    do {
        val v = read()
        items.add(v)
    } while (v >= 128)
    items.reverse()
    val out = items.joinToString(""){(it and 0b01111111).toString(2).padStart(7, '0')}.toLong(2)
    return Triple(items[0], out, items.size)
}