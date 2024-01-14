import java.io.FileNotFoundException
import java.util.*

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    signing
}

group = "io.violabs"
version = "1.0.0"

dependencies {
    implementation(project(":unitSim"))
}

allprojects {
    group = "io.violabs"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    tasks.withType<Test>  {
        useJUnitPlatform()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    dependencies {

    }

}

kotlin {
    jvmToolchain(20)
}

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply { load(it) }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun readFileContent(fileName: String): String {
    val file = File(fileName)
    if (!file.exists()) {
        throw FileNotFoundException("File $fileName does not exist.")
    }
    return file.readText()
}