plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.strimup.android.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.core.database"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
