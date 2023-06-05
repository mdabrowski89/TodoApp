plugins {
    id("config-android-lib")
}

android {
    namespace = "pl.mobite.lib.utilites"
}

dependencies {
    implementation(Deps.fragmentKtx)
    implementation(Deps.recyclerView)
}
