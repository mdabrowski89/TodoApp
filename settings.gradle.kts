@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TodoApp"

// https://medium.com/bumble-tech/how-to-use-composite-builds-as-a-replacement-of-buildsrc-in-gradle-64ff99344b58
includeBuild("gradle-config")

include(
    ":app",
    ":feature:home",
    ":feature:todo-list",
    ":feature:geo-notes",
    ":feature:routines",
    ":library:view-binding",
    ":library:mvi",
    ":library:utilities"
)
