package uk.co.jrtapsell.kGit2

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import uk.co.jrtapsell.kGit2.plumbing.CommitFactory
import uk.co.jrtapsell.kGit2.plumbing.CommitFactory.get
import uk.co.jrtapsell.kGit2.plumbing.Refs

/**
 * @author James Tapsell
 */
class TestCommits {
    @DataProvider(name = "commits")
    fun getTest(): Array<Array<String>> {
        return listOf(
                "1997c13c59526dc740adbe0cb2d662247e8b9154",
                "91c6e76f7976e51873550c699144dc82d05e91ea"
        ).map { arrayOf(it) }.toTypedArray()
    }

    @Test(dataProvider = "commits")
    fun display(commitHash: String) {
        val commit = CommitFactory.get(repo, Hash(commitHash))
        println(commit)
    }

    @Test
    fun masterBack() {
        val repo = Repo("/home/james/git/git/")
        val masterHead = Refs.getLocal(repo, "master")

        val visited = HashSet<Hash>()
        visited.add(masterHead)

        val toVisit = mutableListOf(masterHead)

        while (toVisit.isNotEmpty()) {
            get(repo, toVisit.removeAt(0))
                .parent
                .forEach {
                    if (!visited.contains(it)) {
                        toVisit.add(it)
                        visited.add(it)
                    }
                }
        }

    }
}