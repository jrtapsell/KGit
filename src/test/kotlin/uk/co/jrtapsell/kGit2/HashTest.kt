package uk.co.jrtapsell.kGit2

import org.testng.Assert
import org.testng.annotations.Test
import uk.co.jrtapsell.kGit2.extensions.unpack
import uk.co.jrtapsell.kGit2.plumbing.Refs
import java.util.*

class HashTest {

    val RNG = Random()

    val hashNumbers = (0 until 20)
        .map { RNG.nextInt(256) }

    val hashText = hashNumbers
        .joinToString("") { it.toString(16).padStart(2, '0') }

    val hashBytes = hashNumbers.map { it.toByte() }.toByteArray()


    val hashFromText = Hash(hashText)
    val hashFromBytes = Hash(hashBytes)

    @Test
    fun testEquals() {
        hashFromText assertEquals hashFromBytes
    }

    @Test
    fun testBytes() {
        for (length in hashNumbers.indices) {
            val expected = hashNumbers.take(length)
            val actual = hashFromText.prefix(length).map { it.unpack() }.toList()
            actual assertEquals expected
        }
    }

    @Test
    fun listRefs() {
        Refs.listAll(Repo("/home/james/git/git/"))
    }
}