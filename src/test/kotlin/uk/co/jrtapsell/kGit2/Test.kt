package uk.co.jrtapsell.kGit2

import uk.co.jrtapsell.kGit2.plumbing.Disk

/**
 * @author James Tapsell
 */
fun main(args: Array<String>) {
    val hash = Hash("0015ea8587a0e6d6da5fa0e4a1c4aec217117f31")
    val repo = Repo("/home/james/FYP/android_app_verifier")

    Disk.getObjectByHash(repo, hash).use {
        println(hash)
    }

}