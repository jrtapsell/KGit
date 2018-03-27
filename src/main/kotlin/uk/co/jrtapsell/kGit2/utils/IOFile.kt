package uk.co.jrtapsell.kGit2.utils

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.InflaterInputStream

class IOFile(val filename: String, val fromMemory: Boolean = false): InputStream() {

    companion object {
        fun getData(filename: String, fromMemory: Boolean): InputStream {
            if (!fromMemory) return FileInputStream(filename)
            val buffer = File(filename).readBytes()
            return ByteArrayInputStream(buffer)
        }
    }

    override fun read(): Int {
        return current.read()
    }

    fun setCompressed() {
        current = compressedStream
    }

    fun setUncompressed() {
        current = inputStream
    }

    val inputStream = getData(filename, fromMemory)
    val compressedStream = InflaterInputStream(inputStream)

    var current: InputStream = inputStream

    fun fill(buffer: ByteArray) {
        val read = current.read(buffer)
        if (read != buffer.size) throw AssertionError("Not enough data")
    }
}
