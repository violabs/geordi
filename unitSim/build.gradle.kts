import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

version = "1.0.0-SNAPSHOT"

plugins {
    jacoco
    java
    id("io.gitlab.arturbosch.detekt") version "1.23.4"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.9.10"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    implementation("io.mockk:mockk:1.13.5")
    implementation(kotlin("stdlib"))
    implementation(kotlin("test"))
    implementation(project(":core"))
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
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

tasks.named<DokkaTask>("dokkaJavadoc") {
    dokkaSourceSets {
        named("main") {
            includeNonPublic.set(false)
            skipDeprecated.set(true)
            jdkVersion.set(8)
            sourceLink {
                val uri: URI = URI.create("https://github.com/violabs/geordi")
                this.remoteUrl.set(uri.toURL())
                this.remoteLineSuffix.set("#L")
                this.localDirectory.set(project.projectDir)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            from(components["kotlin"])

            groupId = "io.violabs.geordi"
            artifactId = "unitsim"

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

            // Attaching sources
            val sourcesJar by tasks.registering(Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }
            artifact(sourcesJar)
        }
    }
}

