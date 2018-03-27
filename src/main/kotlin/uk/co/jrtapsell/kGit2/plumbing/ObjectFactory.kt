package uk.co.jrtapsell.kGit2.plumbing

import uk.co.jrtapsell.kGit2.Hash
import uk.co.jrtapsell.kGit2.Repo
import java.io.InputStream
import kotlin.reflect.KClass

abstract class DataObject(val type: ObjectType)

abstract class ObjectFactory<T: DataObject>(val expectedType: ObjectType) {
    fun get(repo: Repo, hash: Hash): T {
        val obj = Disk.getObjectByHash(repo, hash) ?: throw AssertionError("Missing object")
        if (obj.type != expectedType) throw AssertionError("Bad type")
        return parse(obj.content)
    }

    abstract fun parse(inputStream: InputStream): T
}