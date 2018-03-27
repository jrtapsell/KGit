import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
}

group = "uk.co.jrtapsell"
version = "1.0-SNAPSHOT"

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.10"

    repositories {
        mavenCentral()
    }
    
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
    
}

apply {
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testCompile(group= "org.testng", name= "testng", version= "6.14.2")
    compile(group= "commons-codec", name="commons-codec", version= "1.11")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}