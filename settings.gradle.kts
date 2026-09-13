import org.gradle.api.initialization.resolve.RepositoriesMode

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "jing-trang"

include(":util")
include(":resolver")
include(":datatype")
include(":regex-gen")
include(":regex")
include(":xsd-datatype")
include(":legacy-infer")
include(":jing")
include(":trang")
