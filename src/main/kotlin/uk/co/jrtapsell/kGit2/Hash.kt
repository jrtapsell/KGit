package uk.co.jrtapsell.kGit2

import uk.co.jrtapsell.kGit2.extensions.pack
import uk.co.jrtapsell.kGit2.extensions.toHexString
import uk.co.jrtapsell.kGit2.utils.SequenceByteArray
import java.util.*

data class Hash private constructor(private val payload: SequenceByteArray) {
    fun text() = payload.toHexString()

    companion object {
        operator fun invoke(text: String): Hash {
            val bytes = text
                .chunked(2)
                .map { it.toInt(16) }
                .map(Int::pack)
                .toByteArray()
            return Hash(bytes)
        }

        operator fun invoke(bytes: ByteArray): Hash {
            return Hash(SequenceByteArray(bytes))
        }
    }

    fun prefix(bytes: Int) = payload.asSequence().take(bytes)
    fun asciiPrefix(bytes: Int) = prefix(bytes).toHexString()

    fun suffix(bytes: Int) = payload.asSequence().drop(20-bytes).take(bytes)
    fun asciiSuffix(bytes: Int) = suffix(bytes).toHexString()

    override fun toString() = "Hash(${text()})"

    override fun equals(other: Any?): Boolean {
        if (other !is Hash) return false
        return payload.equals(other.payload)
    }

    override fun hashCode(): Int {
        return payload.take(4).fold(0) {acc, value -> (acc * 256) + value}
    }
}