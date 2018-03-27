package uk.co.jrtapsell.kGit2

import org.testng.Assert
import org.testng.annotations.Test
import uk.co.jrtapsell.kGit2.extensions.readLittleVary
import java.io.ByteArrayInputStream

class ReadLittleVaryTest {
    @Test
    fun tes() {
        val backing = arrayOf(162, 22, 246, 21, 144, 60, 145, 85, 38, 48, 105, 109, 112, 111, 114, 116, 32, 117, 107, 46, 99, 111, 46, 106, 114, 116, 97, 112, 115, 101, 108, 108, 46, 102, 121, 112, 46, 98, 97, 115, 101, 85, 116, 105, 108, 115, 46, 116, 101, 115, 116, 85, 116, 105, 108, 115, 46, 42, 177, 121, 79, 1, 32, 40, 100, 101, 108, 116, 97, 32, 62, 61, 32, 109, 105, 110, 105, 109, 117, 109, 77, 83, 41, 46, 97, 115, 115, 101, 114, 116, 84, 114, 117, 101, 40, 147, 238, 1, 45, 32, 40, 100, 101, 108, 116, 97, 32, 60, 61, 32, 109, 97, 120, 105, 109, 117, 109, 77, 83, 41, 46, 97, 115, 115, 101, 114, 116, 84, 114, 117, 101, 40, 179, 65, 2, 32, 1, 28, 108, 105, 110, 101, 115, 46, 115, 105, 122, 101, 32, 97, 115, 115, 101, 114, 116, 78, 111, 116, 69, 113, 117, 97, 108, 115, 32, 48, 147, 146, 3, 189, 1, 53, 179, 80, 4, 241, 2, 28, 112, 114, 111, 46, 101, 120, 105, 116, 67, 111, 100, 101, 32, 97, 115, 115, 101, 114, 116, 69, 113, 117, 97, 108, 115, 32, 32, 49, 179, 120, 7, 43, 1, 19, 111, 117, 116, 112, 117, 116, 32, 97, 115, 115, 101, 114, 116, 69, 113, 117, 97, 108, 115, 147, 190, 8, 22, 147, 79, 8, 5, 179, 218, 8, 72, 2
        ).map { it.toByte() }.toByteArray()
        val input = ByteArrayInputStream(backing)
        val value = input.readLittleVary()
        Assert.assertEquals(value.second, 2850L)
        val value2 = input.readLittleVary()
        Assert.assertEquals(value2.second, 2806L)
    }

}