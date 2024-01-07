version = "1.0.0-SNAPSHOT"

plugins {
    jacoco
    java
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    implementation("io.mockk:mockk:1.13.5")
    implementation(kotlin("test"))
    implementation(project(":core"))
}

tasks.withType<Test>  {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    // Depends on the 'test' task defined by the Java plugin
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/io/violabs/geordi/examples/**")
            }
        })
    )
}