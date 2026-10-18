plugins {
    alias(libs.plugins.strimup.android.library)
    alias(libs.plugins.strimup.hilt)
}

android {
    namespace = "com.strimup.core.user"
}

dependencies {
    api(projects.core.database)

    implementation(libs.kotlinx.coroutines.android)
}
