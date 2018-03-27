package uk.co.jrtapsell.kGit2.utils

class SequenceByteArray(private val data: ByteArray): Sequence<Byte> {

    override fun iterator() = data.iterator()

    operator fun set(index: Int, value: Byte) {
        data[index] = value
    }

    operator fun get(index: Int) = data[index]

    override fun toString() = data.contentToString()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is SequenceByteArray) return false
        val otherData = other.data
        return otherData.size == data.size && data.zip(otherData).none { (a,b) -> a != b}
    }
}