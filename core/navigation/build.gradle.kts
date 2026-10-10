plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.strimup.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
