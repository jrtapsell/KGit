package uk.co.jrtapsell.kGit2

import org.testng.annotations.Test
import uk.co.jrtapsell.kGit2.plumbing.Disk

class TestDisk {

    @Test
    fun getCommitUnpacked() {
        val hash = Hash("1997c13c59526dc740adbe0cb2d662247e8b9154")

        Disk.getObjectByHash(repo, hash)!!.content.bufferedReader().use {
            println(it.readText())
        }
    }

    @Test
    fun getCommitPacked() {
        val hash = Hash("91c6e76f7976e51873550c699144dc82d05e91ea")

        Disk.getObjectByHash(repo, hash)!!.content.bufferedReader().use {
            println(it.readText())
        }
    }
}