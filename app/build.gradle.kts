import java.util.Properties

plugins {
    alias(libs.plugins.strimup.android.application)
    alias(libs.plugins.strimup.android.compose)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.favorite)
    implementation(projects.core.legal)
    implementation(projects.core.navigation)
    implementation(projects.core.network)
    implementation(projects.core.security)
    implementation(projects.core.streamer)
    implementation(projects.core.tag)
    implementation(projects.core.ui)
    implementation(projects.core.user)
    implementation(projects.core.util)
    implementation(projects.feature.account)
    implementation(projects.feature.auth)
    implementation(projects.feature.favorite)
    implementation(projects.feature.filter)
    implementation(projects.feature.home)
    implementation(projects.feature.notification)
    implementation(projects.feature.push)
    implementation(projects.feature.report)
    implementation(projects.feature.schedule)
    implementation(projects.feature.search)
    implementation(projects.feature.streamerdetail)
    implementation(projects.feature.streamerprofile)
    implementation(projects.feature.streamervideos)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.coil)
    implementation(libs.coil.network)
    implementation(libs.hilt.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    annotationProcessor(libs.androidx.lifecycle.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    testImplementation(projects.core.common)
    testImplementation(projects.core.testing)
    testImplementation(projects.core.ui)
    testImplementation(projects.core.user)
    testImplementation(projects.feature.auth)
    testImplementation(projects.feature.notification)
    testImplementation(projects.feature.push)
}
