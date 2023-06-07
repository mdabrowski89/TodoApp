plugins {
    id("config-android-lib")
}

android {
    namespace = "pl.mobite.lib.viewbinding"
}

dependencies {
    implementation(Deps.appCompat)
    implementation(Deps.fragmentKtx)
}
