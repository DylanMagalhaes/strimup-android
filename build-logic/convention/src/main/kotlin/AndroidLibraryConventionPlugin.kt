import com.android.build.api.dsl.LibraryExtension
import com.strimup.buildlogic.configureKotlinAndroid
import com.strimup.buildlogic.configureTestDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "strimup.detekt")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = 36
                lint.targetSdk = 36
            }

            configureTestDependencies()
        }
    }
}
