@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {

    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    compileOptions {
        sourceCompatibility = BuildConfig.javaCompatibility
        targetCompatibility = BuildConfig.javaCompatibility
    }

    kotlinOptions {
        jvmTarget = BuildConfig.jvmTarget
    }

    buildFeatures {
        viewBinding = BuildConfig.viewBinding
    }
}
