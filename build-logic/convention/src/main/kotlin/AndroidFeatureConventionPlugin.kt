import com.strimup.buildlogic.libs
import com.strimup.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "strimup.android.library")
            apply(plugin = "strimup.android.compose")
            apply(plugin = "strimup.hilt")

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", libs.library("hilt-viewmodel-compose"))
                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel"))

                add("testImplementation", project(":core:testing"))
            }
        }
    }
}
