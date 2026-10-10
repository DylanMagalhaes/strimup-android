plugins {
    alias(libs.plugins.strimup.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.feature.streamerprofile"
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.core.network)
    implementation(projects.core.streamer)
    implementation(projects.core.tag)
    implementation(projects.core.user)
    implementation(projects.feature.schedule)
    implementation(projects.feature.streamervideos)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(projects.core.common)
}
