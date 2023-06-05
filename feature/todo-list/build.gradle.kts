plugins {
    id("config-android-lib")
    id("kotlin-parcelize")
}

android {
    namespace = "${AppConfig.applicationId}.todolist"
}

dependencies {
    implementation(project(":library:view-binding"))
    implementation(project(":library:mvi"))
    implementation(project(":library:utilities"))

    implementation(Deps.fragmentKtx)
    implementation(Deps.constraintLayout)
    implementation(Deps.recyclerView)
}
