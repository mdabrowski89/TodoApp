plugins {
    id("config-android-lib")
}

android {
    namespace = "${AppConfig.applicationId}.home"
}

dependencies {

    implementation(project(":feature:todo-list"))
    implementation(project(":feature:geo-notes"))
    implementation(project(":feature:routines"))

    implementation(project(":library:view-binding"))

    implementation(Deps.fragmentKtx)
    implementation(Deps.material)
    implementation(Deps.navigationFragmentKtx)
    implementation(Deps.navigationUiKtx)
}
