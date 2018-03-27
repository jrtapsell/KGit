package uk.co.jrtapsell.kGit2.plumbing

import uk.co.jrtapsell.kGit2.Hash
import uk.co.jrtapsell.kGit2.Repo

object Refs {
    fun get(repo: Repo, vararg name: String): Hash {
        val plainFile = name.fold(repo.gitFolder) {file, name -> file.resolve(name)}
        if (plainFile.exists()) {
            val text = plainFile
                .readText()
                .trim()
            return Hash(text)
        } else {
            val slashName = (listOf("refs") + name).joinToString("/")
            return repo.gitFolder.resolve("packed-refs")
                .useLines {
                    it.map {
                        it.split(" ")
                    }.first { (_, ref) ->
                        ref == slashName
                    }
                        .get(0)
                        .let { Hash(it) }
                }
        }
    }

    fun getLocal(repo: Repo, name: String) = get(repo, "heads", name)

    val packedRegex = Regex("([0-9a-f]{40}) ([^\\s]+)(?:\\n\\^([0-9a-f]{40}))?")
    fun listAll(repo: Repo): String {
        val refsDir = repo.gitFolder.resolve("refs")
        val raw = refsDir.walk()
            .filter { it.isFile }
            .map { it.toRelativeString(refsDir) }
            .toList()
        val packedText = repo.gitFolder
            .resolve("packed-refs")
            .readText()
        val packed = packedRegex.findAll(packedText)
        println(raw + packed)
        TODO()
    }
}