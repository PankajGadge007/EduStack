pluginManagement {
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Edu Stack"

include(":app")
include(":core:api")
include(":core:common")
include(":core:database")
include(":core:domain")
include(":core:firebase")
include(":core:impl")
include(":core:ui")
include(":designsystem")
include(":feature:auth")
include(":feature:help")
include(":feature:practical")
include(":feature:quiz")
include(":security")