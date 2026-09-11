plugins {
    `java-library`
}

val generatedDir = layout.buildDirectory.dir("generated/sources/regex/main")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    api(project(":util"))
    implementation("xerces:xercesImpl:2.12.2")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val regexGenerator by configurations.creating

dependencies {
    regexGenerator(project(":regex-gen"))
}

sourceSets {
    named("main") {
        java.srcDir(generatedDir)
        resources {
            srcDir("src/main/java")
            include("**/resources/**")
        }
    }
}

val generateNamingExceptions by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generate NamingExceptions source used by regex translator"
    mainClass.set("com.thaiopensource.datatype.xsd.regex.java.gen.NamingExceptionsGen")
    classpath = regexGenerator
    args(
        "com.thaiopensource.datatype.xsd.regex.java.NamingExceptions",
        generatedDir.get().asFile.absolutePath
    )
    outputs.file(generatedDir.map {
        it.file("com/thaiopensource/datatype/xsd/regex/java/NamingExceptions.java")
    })
}

val generateCategories by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generate Unicode category source used by regex translator"
    mainClass.set("com.thaiopensource.datatype.xsd.regex.java.gen.CategoriesGen")
    classpath = regexGenerator
    args(
        "com.thaiopensource.datatype.xsd.regex.java.Categories",
        generatedDir.get().asFile.absolutePath,
        rootProject.file("src/main/resources/lib/UnicodeData-3.1.0.txt").absolutePath
    )
    outputs.file(generatedDir.map {
        it.file("com/thaiopensource/datatype/xsd/regex/java/Categories.java")
    })
}

tasks.named("compileJava") {
    dependsOn(generateNamingExceptions, generateCategories)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
    testLogging {
        events("failed", "skipped")
        showExceptions = true
        showStackTraces = true
    }
}

