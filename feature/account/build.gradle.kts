plugins {
    alias(libs.plugins.strimup.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.feature.account"
}

dependencies {
    implementation(projects.core.legal)
    implementation(projects.core.network)
    implementation(projects.core.user)
    implementation(projects.feature.auth)
    implementation(projects.feature.push)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.coil)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)

    testImplementation(projects.core.common)
    testImplementation(libs.okhttp3)
}
