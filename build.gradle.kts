import org.gradle.api.tasks.SourceSetContainer
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.math.BigDecimal

plugins {
    `java-library`
    jacoco
    `maven-publish`
    id("com.github.spotbugs") version "6.5.11"
    id("org.owasp.dependencycheck") version "13.0.0"
}

group = "org.relaxng"

val xmlVersionFile = file("src/main/resources/version.xml")
val baseVersion = if (xmlVersionFile.exists()) {
    Regex("<version>([^<]+)</version>")
        .find(xmlVersionFile.readText())
        ?.groupValues
        ?.get(1)
        ?.trim()
        .orEmpty()
        .ifEmpty { "0.0.0" }
} else {
    "0.0.0"
}

val snapshotRequested = providers.gradleProperty("snapshot").orNull == "true" ||
    gradle.startParameter.taskNames.any { it == "releaseSnapshot" || it == "snapshot" }

version = if (snapshotRequested) "$baseVersion-SNAPSHOT" else baseVersion

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}

configurations.configureEach {
    resolutionStrategy {
        failOnVersionConflict()
    }
}

dependencyLocking {
    lockAllConfigurations()
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<org.gradle.testing.jacoco.tasks.JacocoReport>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

val coveredProjects = listOf(
    project(":util"),
    project(":resolver"),
    project(":datatype"),
    project(":regex-gen"),
    project(":regex"),
    project(":xsd-datatype"),
    project(":legacy-infer")
)

val jacocoRootReport = tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate JaCoCo report for migrated and onboarded modules."
    dependsOn(coveredProjects.map { it.tasks.named("test") })

    executionData.from(coveredProjects.map { it.layout.buildDirectory.file("jacoco/test.exec") })
    sourceDirectories.from(coveredProjects.map { it.extensions.getByType(SourceSetContainer::class.java).named("main").get().allSource.srcDirs })
    classDirectories.from(coveredProjects.map { it.extensions.getByType(SourceSetContainer::class.java).named("main").get().output })

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

val jacocoRootCoverageVerification = tasks.register<JacocoCoverageVerification>("jacocoRootCoverageVerification") {
    group = "verification"
    description = "Checks aggregate line coverage for migrated and onboarded modules."
    dependsOn(jacocoRootReport)

    executionData.from(coveredProjects.map { it.layout.buildDirectory.file("jacoco/test.exec") })
    sourceDirectories.from(coveredProjects.map { it.extensions.getByType(SourceSetContainer::class.java).named("main").get().allSource.srcDirs })
    classDirectories.from(coveredProjects.map { it.extensions.getByType(SourceSetContainer::class.java).named("main").get().output })

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = BigDecimal("0.20")
            }
        }
    }
}

tasks.named("check") {
    dependsOn(jacocoRootCoverageVerification)
}

dependencyCheck {
    formats = listOf(org.owasp.dependencycheck.reporting.ReportGenerator.Format.SARIF.toString())
    outputDirectory = layout.buildDirectory.dir("reports")
    nvd.apiKey = providers.environmentVariable("NVD_API_KEY").orNull
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        showExceptions = true
        showStackTraces = true
    }
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
    effort = com.github.spotbugs.snom.Effort.DEFAULT
    reportLevel = com.github.spotbugs.snom.Confidence.MEDIUM
    reports {
        create("html") {
            required = true
            outputLocation = layout.buildDirectory.file("reports/spotbugs/${name}.html")
        }
    }
}

tasks.named<org.gradle.api.tasks.wrapper.Wrapper>("wrapper") {
    gradleVersion = "9.5.1"
    distributionType = org.gradle.api.tasks.wrapper.Wrapper.DistributionType.BIN
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

tasks.register("release") {
    group = "release"
    description = "Replacement for release.py default command: clean + build"
    dependsOn("clean", "build")
}

tasks.register("releaseBuild") {
    group = "release"
    description = "Replacement for release.py build command"
    dependsOn("release")
}

tasks.register("publishRelease") {
    group = "release"
    description = "Replacement for release.py publish command"
    dependsOn("publish")
}

tasks.register("releaseSnapshot") {
    group = "release"
    description = "Replacement for release.py snapshot command (publishes with -SNAPSHOT version)"
    dependsOn("publish")
}

tasks.register("snapshot") {
    group = "release"
    description = "Alias for releaseSnapshot"
    dependsOn("releaseSnapshot")
}

