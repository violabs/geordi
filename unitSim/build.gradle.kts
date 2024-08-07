import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.DokkaTask
import java.io.FileNotFoundException
import java.net.URI

version = "1.0.6"

plugins {
    jacoco
    java
    `maven-publish`
    signing
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.junit.jupiter:junit-jupiter-api:5.11.0-M2")
    implementation("io.mockk:mockk:1.13.10")
    implementation(kotlin("stdlib"))
    implementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.withType<Test>  {
    useJUnitPlatform()
    finalizedBy(tasks.koverHtmlReport)
    finalizedBy(tasks.koverXmlReport)
}

koverReport {
    filters {
        excludes {
            // exclusion rules - classes to exclude from report
            classes("io.violabs.geordi.examples.**")
        }
    }
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
//    config.setFrom("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
//    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = JavaVersion.VERSION_11.majorVersion
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = JavaVersion.VERSION_11.majorVersion
}

tasks.named<DokkaTask>("dokkaJavadoc") {
    dokkaSourceSets {
        named("main") {
            includeNonPublic.set(true)
            skipDeprecated.set(true)
            jdkVersion.set(11)
            sourceLink {
                val uri: URI = URI.create("https://github.com/violabs/geordi")
                this.remoteUrl.set(uri.toURL())
                this.remoteLineSuffix.set("#L")
                this.localDirectory.set(project.projectDir)
            }
        }
    }
}

tasks.jar {
    exclude("**/examples/**")
}

publishing {
    publications {
        repositories {
            val ossrhUsername: String by project
            val ossrhPassword: String by project
            maven {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
            maven {
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }

        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])

            artifactId = "unit-sim"

            // Project information
            pom {
                name.set("Geordi - Next Generation Testing Framework")
                description.set("""
                    Geordi Test Framework is a Kotlin-based testing framework integrating with
                    JUnit 5's TestTemplate for dynamic and parameterized testing. It supports file-based
                    and parameter-based scenarios, suitable for various testing contexts. Key features
                    include dynamic test case generation, SimulationGroup for scenario organization, and
                    integration with JUnit 5's advanced features. It also includes a utility class, UnitSim,
                    for method-level testing and mocking with Mockk. Geordi is inspired by the Spock framework,
                    aiming to provide comparable functionality in a Kotlin-optimized package.
                """.trimIndent())
                url.set("https://github.com/violabs/geordi")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("joshstallnick")
                        name.set("Josh Stallnick")
                        organization.set("Violabs Software")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/violabs/geordi.git")
                    developerConnection.set("scm:git:ssh://github.com:violabs/geordi.git")
                    url.set("https://github.com/violabs/geordi")
                }
            }

            // Attaching Dokka-generated documentation
            val dokkaJavadocJar by tasks.registering(Jar::class) {
                archiveClassifier.set("javadoc")
                from(tasks["dokkaJavadoc"])
            }
            artifact(dokkaJavadocJar)

            val dokkaHtmlJar by tasks.registering(Jar::class) {
                archiveClassifier.set("kdoc")
                from(tasks["dokkaHtml"])
            }
            artifact(dokkaHtmlJar)

            // Attaching sources
            val sourcesJar by tasks.registering(Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }
            artifact(sourcesJar)
        }
    }
}

fun readFileContent(fileName: String): String {
    var file = File(fileName)
    if (!file.exists()) {
        file = File("../$fileName")
        if (!file.exists()) throw FileNotFoundException("File $fileName does not exist.")
    }
    return file.readText()
}


signing {
    val keyId = findProperty("signing.keyId") as String?
    val secretKeyFile = findProperty("signing.secretKeyFile") as String?
    val password = findProperty("signing.password") as String?

    val secretKey: String? = secretKeyFile
        ?.let { readFileContent(it) }
        ?: (findProperty("signing.secretKey") as String?)

    useInMemoryPgpKeys(keyId, secretKey, password)
    sign(publishing.publications)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17) // Specify your desired Java version here
    }
}