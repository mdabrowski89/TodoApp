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
include(
    ":app",
    ":feature:home",
    ":feature:todo-list",
    ":feature:geo-notes",
    ":feature:routines",
    ":library:view-binding"
)
