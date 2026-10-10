plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.core.streamer"
}

dependencies {
    api(projects.core.network)
    api(projects.core.tag)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.exifinterface)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp3)
    implementation(libs.retrofit)
}
