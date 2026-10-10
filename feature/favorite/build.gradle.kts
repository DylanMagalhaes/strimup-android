plugins {
    alias(libs.plugins.strimup.android.feature)
}

android {
    namespace = "com.strimup.feature.favorite"
}

dependencies {
    implementation(projects.core.favorite)
    implementation(projects.core.network)
    implementation(projects.core.streamer)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.common)
}
