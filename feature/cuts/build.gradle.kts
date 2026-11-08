plugins {
    alias(libs.plugins.strimup.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.feature.cuts"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.streamer)
    implementation(projects.core.user)
    implementation(projects.feature.report)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.retrofit)

    testImplementation(projects.core.common)
    testImplementation(libs.okhttp3)
}
