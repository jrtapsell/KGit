package uk.co.jrtapsell.kGit2

import org.testng.Assert

infix fun Any?.assertEquals(other: Any?) {
    Assert.assertEquals(this, other)
}

val repo = Repo("/home/james/FYP/android_app_verifier")