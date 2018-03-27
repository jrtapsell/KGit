package uk.co.jrtapsell.kGit2

import org.testng.annotations.Test
import uk.co.jrtapsell.kGit2.extensions.readGitVaryBig
import java.io.ByteArrayInputStream

class TestExtensions {
    @Test
    fun testReadInt() {
        val data = listOf(0b10000111, 0b00001110, 0b10101010)
        val backing = data.map { it.toByte() }.toByteArray()

        val baos = ByteArrayInputStream(backing)

        val (first, value) = baos.readGitVaryBig(4)

        first assertEquals 0b10000111
        value assertEquals 0b111100000100000010L
        baos.read() assertEquals 0b10101010
    }
}