plugins {
    kotlin("jvm") version "1.9.21"
}

group = "io.violabs"
version = "1.0-SNAPSHOT"

allprojects {
    group = "io.violabs"

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>  {
        useJUnitPlatform()
    }
}

subprojects {
    apply {
//        plugin("kotlin")
        plugin("org.jetbrains.kotlin.jvm")
    }

    dependencies {

    }

}

kotlin {
    jvmToolchain(20)
}