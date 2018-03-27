package uk.co.jrtapsell.kGit2.plumbing

import uk.co.jrtapsell.kGit2.Hash
import uk.co.jrtapsell.kGit2.Repo
import java.io.InputStream

data class Commit(
    private val signedBody: String,
    val tree: Hash,
    val parent: List<Hash>,
    val author: String,
    val committer: String,
    val title: String,
    val body: String,
    val signature: String?): DataObject(ObjectType.COMMIT)

object CommitFactory: ObjectFactory<Commit>(ObjectType.COMMIT) {
    override fun parse(inputStream: InputStream): Commit {
        val text = inputStream
            .bufferedReader()
            .use { it.readText() }

        val match = signatureRegex.find(text)?.groupValues?.get(1)
        val noSig = text.replace(signatureRegex, "")
        val lines = noSig.lines()
        val headerEnd = lines.indexOf("")
        val header = lines.subList(0, headerEnd)
            .map {
                val (name, value) = it.split(Regex(" "),2)
                name to value
            }
            .groupBy({ it.first }, {it.second})
        val message = lines.subList(headerEnd+1, lines.size)
        val title = message[0]
        val body = message.subList(1, message.size).joinToString(System.lineSeparator())
        return Commit(
                noSig,
                Hash(header["tree"]!![0]),
                header["parent"]?.map { Hash(it) }?: listOf(),
                header["author"]!![0],
                header["committer"]!![0], title, body, match)
    }

    val signatureRegex = Regex(
            "\ngpgsig (-----BEGIN PGP SIGNATURE-----.*-----END PGP SIGNATURE-----)",
            RegexOption.DOT_MATCHES_ALL)
}