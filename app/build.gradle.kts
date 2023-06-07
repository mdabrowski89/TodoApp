plugins {
    id("config-android-app")
}

android {
    namespace = AppConfig.applicationId
}

dependencies {
    implementation(project(":feature:home"))

    implementation(Deps.appCompat)

    testImplementation(Deps.junit)

    androidTestImplementation(Deps.junit)
    androidTestImplementation(Deps.espressoCore)
}
