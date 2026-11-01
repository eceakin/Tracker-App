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

rootProject.name = "NamazTakibi"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:prayertimes")
include(":feature:calendar")
include(":feature:kaza")
include(":feature:mazeret")
include(":feature:onboarding")
include(":feature:settings")
include(":feature:today")
include(":widget")
