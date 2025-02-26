enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "suspendapp-runner"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
    repositories {
        mavenCentral()
    }
}

include(":common")
include(":jvm")
include(":tester")
