package uk.co.jrtapsell.kGit2

import uk.co.jrtapsell.kGit2.plumbing.Disk

fun main(args: Array<String>) {
    val FYP = Repo("/home/james/FYP/android_app_verifier")
    val repo = Disk.allHashes(FYP).map {
        Disk.getObjectByHash(FYP, it)!!
    }
}