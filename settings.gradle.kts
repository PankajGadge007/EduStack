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
include(":security")
include(":designsystem")
include(":feature:help")
include(":feature:practical")
include(":feature:auth")
include(":feature:quiz")
include(":core:ui")
include(":core:api")
include(":core:common")
include(":core:domain")
include(":core:impl")
include(":core:database")
include(":core:firebase")
