plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.android.compose)
}

android {
    namespace = "com.strimup.core.ui"
}

dependencies {
    api(projects.core.common)
    api(projects.core.streamer)
    api(projects.core.tag)
    api(projects.core.user)
    api(projects.core.util)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
}
