import java.io.FileNotFoundException
import java.util.*

plugins {
    kotlin("jvm") version "1.9.21"
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "io.violabs.geordi"

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    tasks.withType<Test>  {
        useJUnitPlatform()
    }
}

subprojects {
    group = "io.violabs.geordi"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    dependencies {

    }

}

kotlin {
    jvmToolchain(11)
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
    ext["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

fun readFileContent(fileName: String): String {
    var file = File(fileName)
    if (!file.exists()) {
        file = File("../$fileName")
        if (!file.exists()) throw FileNotFoundException("File $fileName does not exist.")
    }
    return file.readText()
}

nexusPublishing {
    val ossrhUsername: String by project
    val ossrhPassword: String by project

    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}