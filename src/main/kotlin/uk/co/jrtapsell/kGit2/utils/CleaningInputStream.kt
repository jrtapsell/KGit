package uk.co.jrtapsell.kGit2.utils

import java.io.Closeable
import java.io.InputStream

/**
 * @author James Tapsell
 */
class CloseGroup(vararg val others: Closeable): Closeable {

    override fun close() {
        for (o in others) {
            o.close()
        }
    }

}