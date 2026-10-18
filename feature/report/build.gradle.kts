plugins {
    alias(libs.plugins.strimup.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.feature.report"
}

dependencies {
    implementation(projects.core.network)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)

    testImplementation(projects.core.common)
    testImplementation(libs.okhttp3)
}
