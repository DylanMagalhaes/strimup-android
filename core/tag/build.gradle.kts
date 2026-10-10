plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.core.tag"
}

dependencies {
    api(projects.core.network)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
}
