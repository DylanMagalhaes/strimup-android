import java.util.Properties

plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.strimup.core.network"

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://strimup-back-fd5v.onrender.com/\"")
    }

    buildTypes {
        debug {
            localProperties.getProperty("BASE_URL")?.let { baseUrl ->
                buildConfigField("String", "BASE_URL", baseUrl)
            }
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.common)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
}
