package uk.co.jrtapsell.kGit2

import java.io.File

data class Repo(val filePath: String) {
    var gitFolder = File(filePath).resolve(".git")
}
