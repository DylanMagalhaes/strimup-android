import androidx.room3.gradle.RoomExtension
import com.strimup.buildlogic.libs
import com.strimup.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            apply(plugin = "androidx.room3")

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                add("implementation", libs.library("androidx-room3-runtime"))
                add("ksp", libs.library("androidx-room3-compiler"))
                add("androidTestImplementation", libs.library("androidx-room3-testing"))
            }
        }
    }
}
