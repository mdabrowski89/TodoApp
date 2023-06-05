plugins {
    id("config-android-lib")
}

android {
    namespace = "pl.mobite.lib.mvi"
}

dependencies {
    implementation(Deps.kotlinxCoroutinesAndroid)
    implementation(Deps.lifecycleViewModelKtx)
    implementation(Deps.lifecycleViewModelSavedstate)
}
