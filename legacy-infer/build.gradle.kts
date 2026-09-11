plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":util"))
    implementation(project(":resolver"))
    implementation(project(":datatype"))
    implementation(project(":xsd-datatype"))
    implementation(project(":regex"))

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    named("main") {
        java.srcDir("../src/main/legacy/mod/infer/src/main")
    }
    named("test") {
        java.srcDir("../src/main/legacy/mod/infer/src/test")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("failed", "skipped")
        showExceptions = true
        showStackTraces = true
    }
}

