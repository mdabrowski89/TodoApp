import org.gradle.api.JavaVersion

object AppConfig {

    const val applicationId = "pl.mobite.todoapp"

    const val versionCode = 1
    const val versionName = "1.0"

    const val minSdk = 21
    const val compileSdk = 34
}

object BuildConfig {

    const val jvmTarget = "17"
    val javaCompatibility = JavaVersion.VERSION_17

    const val viewBinding = true
}


