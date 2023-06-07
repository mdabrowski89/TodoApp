plugins {
    id("config-android-lib")
}

android {
    namespace = "${AppConfig.applicationId}.geonotes"
}

dependencies {
    implementation(project(":library:view-binding"))

    implementation(Deps.fragmentKtx)
}
