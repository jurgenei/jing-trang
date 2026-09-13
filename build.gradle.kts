import org.gradle.api.GradleException
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Zip
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.math.BigDecimal
import java.security.MessageDigest

plugins {
    `java-library`
    jacoco
    `maven-publish`
    signing
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
            artifactId = "jing-trang"

            pom {
                name.set("jing-trang")
                description.set("RELAX NG validator and schema converter (Jing/Trang)")
                url.set("https://github.com/jurgenei/jing-trang")

                licenses {
                    license {
                        name.set("BSD 3-Clause License")
                        url.set("https://opensource.org/license/bsd-3-clause")
                    }
                }

                developers {
                    developer {
                        id.set("jurgenei")
                        name.set("Jurgen Hildebrand")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/jurgenei/jing-trang.git")
                    developerConnection.set("scm:git:ssh://git@github.com/jurgenei/jing-trang.git")
                    url.set("https://github.com/jurgenei/jing-trang")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "sonatype"
            val releasesRepoUrl = "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = providers.gradleProperty("mavenCentralUsername").orNull
                password = providers.gradleProperty("mavenCentralPassword").orNull
            }
        }
    }
}

signing {
    tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
        onlyIf {
            !gradle.startParameter.taskNames.any { name -> name.contains("publishToMavenLocal") }
        }
    }

    setRequired {
        gradle.taskGraph.allTasks.any { task ->
            task.name.startsWith("publish") && !task.name.contains("MavenLocal")
        }
    }

    val signingKey = providers.gradleProperty("signingKey").orNull
    val signingPassword = providers.gradleProperty("signingPassword").orNull
    val signingKeyId = providers.gradleProperty("signingKeyId").orNull

    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        if (!signingKeyId.isNullOrEmpty()) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        } else {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
    } else {
        useGpgCmd()
    }

    sign(publishing.publications)
}

val groupPath = project.group.toString().replace('.', '/')
val centralArtifactIds = listOf("jing", "trang")

val stageCentralBundleRepo = tasks.register<Sync>("stageCentralBundleRepo") {
    dependsOn(
        tasks.named("publishMavenJavaPublicationToMavenLocal"),
        project(":jing").tasks.named("publishMavenJavaPublicationToMavenLocal"),
        project(":trang").tasks.named("publishMavenJavaPublicationToMavenLocal")
    )

    centralArtifactIds.forEach { artifactId ->
        val artifactBaseDir = file("${System.getProperty("user.home")}/.m2/repository/$groupPath/$artifactId")
        val artifactVersionDir = file("$artifactBaseDir/${project.version}")
        from(artifactVersionDir)
        into(layout.buildDirectory.dir("central-staging-repo/$groupPath/$artifactId/${project.version}"))
    }

    doFirst {
        centralArtifactIds.forEach { artifactId ->
            val artifactVersionDir = file(
                "${System.getProperty("user.home")}/.m2/repository/$groupPath/$artifactId/${project.version}"
            )
            if (!artifactVersionDir.exists()) {
                throw GradleException("Expected local Maven artifact version directory not found: $artifactVersionDir")
            }
        }
    }
}

val generateCentralBundleChecksums = tasks.register("generateCentralBundleChecksums") {
    dependsOn(stageCentralBundleRepo)
    notCompatibleWithConfigurationCache("Generates checksum files by scanning staged output directory at execution time.")

    doLast {
        val stagedRepoDir = layout.buildDirectory
            .dir("central-staging-repo/$groupPath")
            .get()
            .asFile

        if (!stagedRepoDir.exists()) {
            throw GradleException("Expected staged repo directory not found: $stagedRepoDir")
        }

        fun checksum(file: java.io.File, algorithm: String): String {
            val digest = MessageDigest.getInstance(algorithm)
            file.inputStream().use { input ->
                val buffer = ByteArray(8192)
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    digest.update(buffer, 0, read)
                }
            }
            return digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xff) }
        }

        stagedRepoDir.walkTopDown()
            .filter { it.isFile && !it.name.endsWith(".md5") && !it.name.endsWith(".sha1") }
            .forEach { file ->
                file.resolveSibling("${file.name}.md5").writeText("${checksum(file, "MD5")}\n")
                file.resolveSibling("${file.name}.sha1").writeText("${checksum(file, "SHA-1")}\n")
            }
    }
}

tasks.register<Zip>("packageCentralBundle") {
    dependsOn(generateCentralBundleChecksums)
    archiveBaseName.set("jing-trang")
    archiveVersion.set(project.version.toString())
    archiveClassifier.set("central-bundle")
    destinationDirectory.set(layout.buildDirectory.dir("central-bundle"))
    from(layout.buildDirectory.dir("central-staging-repo"))}
