pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "strimup"
include(":app")
include(":core:common")
include(":core:database")
include(":core:favorite")
include(":core:legal")
include(":core:navigation")
include(":core:network")
include(":core:security")
include(":core:streamer")
include(":core:tag")
include(":core:testing")
include(":core:ui")
include(":core:user")
include(":core:util")
include(":feature:account")
include(":feature:auth")
include(":feature:favorite")
include(":feature:filter")
include(":feature:home")
include(":feature:notification")
include(":feature:push")
include(":feature:report")
include(":feature:schedule")
include(":feature:search")
include(":feature:streamerdetail")
include(":feature:streamerprofile")
include(":feature:streamervideos")
