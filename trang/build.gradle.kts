import org.gradle.api.publish.maven.MavenPublication
import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}


dependencies {
    implementation(project(":util"))
    implementation(project(":resolver"))
    implementation(project(":datatype"))
    implementation(project(":regex"))
    implementation(project(":xsd-datatype"))
    implementation(project(":legacy-infer"))

    implementation("xml-resolver:xml-resolver:1.2")
    implementation("net.sf.saxon:Saxon-HE:12.5")
    implementation("xalan:xalan:2.7.3")
    implementation("xerces:xercesImpl:2.12.2")
}

val javacc = configurations.create("javacc")

dependencies {
    javacc("net.java.dev.javacc:javacc:4.0")
}

val generateCompactSyntax = tasks.register<JavaExec>("generateCompactSyntax") {
    val outputDir = layout.buildDirectory.dir("generated-src/javacc/com/thaiopensource/relaxng/parse/compact")
    outputs.dir(outputDir)
    classpath = javacc
    mainClass.set("javacc")
    args(
        "-OUTPUT_DIRECTORY=${outputDir.get().asFile.absolutePath}",
        "../src/main/legacy/mod/rng-parse/src/main/com/thaiopensource/relaxng/parse/compact/CompactSyntax.jj"
    )
}

sourceSets {
    named("main") {
        java.srcDir(layout.buildDirectory.dir("generated-src/javacc"))
        java.srcDirs(
            "../src/main/legacy/mod/catalog/src/main",
            "../src/main/legacy/mod/dtd-parse/src/main",
            "../src/main/legacy/mod/rng-schema/src/main",
            "../src/main/legacy/mod/convert-from-xml/src/main",
            "../src/main/legacy/mod/convert-to-xsd/src/main",
            "../src/main/legacy/mod/convert-from-dtd/src/main",
            "../src/main/legacy/mod/convert-to-dtd/src/main",
            "../src/main/legacy/mod/trang/src/main"
        )
    }
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(generateCompactSyntax)
    source(
        fileTree("../src/main/legacy/mod/rng-parse/src/main") {
            include("**/*.java")
            exclude("com/thaiopensource/relaxng/parse/compact/JavaCharStream.java")
        }
    )
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(generateCompactSyntax)
    from(
        fileTree("../src/main/legacy/mod/rng-parse/src/main") {
            include("**/*.java")
            exclude("com/thaiopensource/relaxng/parse/compact/JavaCharStream.java")
        }
    )
}

tasks.withType<Javadoc>().configureEach {
    isFailOnError = false
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "com.thaiopensource.relaxng.translate.Driver"
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "trang"

            pom {
                name.set("trang")
                description.set("Schema converter")
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

