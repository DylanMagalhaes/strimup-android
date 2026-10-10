plugins {
    alias(libs.plugins.strimup.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.feature.notification"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.user)

    implementation(libs.androidx.compose.material.icons)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)

    testImplementation(projects.core.common)
}
