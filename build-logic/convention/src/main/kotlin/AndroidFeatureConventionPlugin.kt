import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

// M-07 — :feature:* modülleri: Android kütüphane + Compose + :core:domain bağımlılığı.
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("namaztakibi.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<LibraryExtension> {
                buildFeatures.apply {
                    compose = true
                }
            }

            val libs = versionCatalog()
            dependencies {
                add("implementation", project(":core:domain"))
                add("implementation", platform(libs.library("androidx-compose-bom")))
                add("implementation", libs.library("androidx-compose-ui"))
                add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
                add("implementation", libs.library("androidx-compose-material3"))
                add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
            }
        }
    }
}
