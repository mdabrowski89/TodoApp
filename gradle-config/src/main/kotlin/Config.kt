import org.gradle.api.JavaVersion

object AppConfig {

    const val applicationId = "pl.mobite.todoapp"

    const val versionCode = 1
    const val versionName = "1.0"

    const val minSdk = 21
    const val compileSdk = 33
}

object BuildConfig {

    const val jvmTarget = "1.8"
    val javaCompatibility = JavaVersion.VERSION_1_8

    const val viewBinding = true
}


