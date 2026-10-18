plugins {
    alias(libs.plugins.strimup.android.library)
}

android {
    namespace = "com.strimup.core.testing"
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
