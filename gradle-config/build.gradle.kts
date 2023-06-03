repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    // Add fake plugin, if you don't have any
    plugins.register("common-config-plugin") {
        id = "common-config-plugin"
        implementationClass = "CommonConfigPlugin"
    }
    // Or provide your implemented plugins
}
