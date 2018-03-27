package uk.co.jrtapsell.kGit2.plumbing

/**
 * @author James Tapsell
 */
enum class ObjectType(val isDelta: Boolean, val textual: String?) {
    EXTENDED(false, null),
    COMMIT(false, "commit"),
    TREE(false, null),
    BLOB(false, null),
    TAG(false, null),
    RESERVED(false, null),
    OFS_DELTA(true, null),
    REF_DELTA(true, null);

    companion object {
        val textMap = values()
            .filter { it.textual != null }
            .associate { it.textual!! to it }

        fun getByTextual(name: String) = textMap[name]
    }
}