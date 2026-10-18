plugins {
    alias(libs.plugins.strimup.android.feature)
}

android {
    namespace = "com.strimup.feature.streamerdetail"
}

dependencies {
    implementation(projects.core.favorite)
    implementation(projects.core.network)
    implementation(projects.core.streamer)
    implementation(projects.core.tag)
    implementation(projects.feature.report)
    implementation(projects.feature.schedule)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(projects.core.common)
}
