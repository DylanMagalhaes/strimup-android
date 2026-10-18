plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.core.favorite"
}

dependencies {
    api(projects.core.database)
    api(projects.core.network)
    api(projects.core.streamer)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
}
