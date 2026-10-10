import java.util.Properties

plugins {
    alias(libs.plugins.strimup.android.application)
    alias(libs.plugins.strimup.android.compose)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.strimup.android.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val keystoreProperties = Properties().apply {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}

fun releaseSigningValue(name: String): String? =
    keystoreProperties.getProperty(name) ?: System.getenv("STRIMUP_RELEASE_${name.uppercase()}")

val releaseStoreFile = releaseSigningValue("storeFile")?.let { path -> rootProject.file(path) }
val hasReleaseSigning = releaseStoreFile?.exists() == true

android {
    namespace = "com.strimup"

    defaultConfig {
        applicationId = "com.strimup"
        versionCode = 3
        versionName = "1.1.1"

        buildConfigField("String", "BASE_URL", "\"https://strimup-back-fd5v.onrender.com/\"")
        manifestPlaceholders["crashlyticsCollectionEnabled"] = true
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = releaseStoreFile
                storePassword = releaseSigningValue("storePassword")
                keyAlias = releaseSigningValue("keyAlias")
                keyPassword = releaseSigningValue("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            localProperties.getProperty("BASE_URL")?.let { baseUrl ->
                buildConfigField("String", "BASE_URL", baseUrl)
            }
        }
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.compose.material.icons)

    implementation(libs.coil)
    implementation(libs.coil.network)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel)
    annotationProcessor(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.interceptor)

    implementation(libs.hilt.viewmodel.compose)

    implementation(libs.androidx.browser)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
}
