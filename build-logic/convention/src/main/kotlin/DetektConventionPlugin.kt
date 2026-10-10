import com.strimup.buildlogic.libs
import com.strimup.buildlogic.library
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "io.gitlab.arturbosch.detekt")

            extensions.configure<DetektExtension> {
                buildUponDefaultConfig = true
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                baseline = rootProject.file("config/detekt/baseline.xml")
                parallel = true
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    sarif.required.set(true)
                    txt.required.set(false)
                }
            }

            dependencies {
                add("detektPlugins", libs.library("detekt-formatting"))
            }
        }
    }
}
