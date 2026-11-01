import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// M-07 — :core:domain gibi saf Kotlin/JVM modülleri için ortak ayarlar.
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = ProjectConfig.JAVA_VERSION
                targetCompatibility = ProjectConfig.JAVA_VERSION
            }
            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(ProjectConfig.JVM_TARGET)
                }
            }

            val libs = versionCatalog()
            dependencies {
                add("testImplementation", platform(libs.library("junit-bom")))
                add("testImplementation", libs.library("junit-jupiter"))
                add("testRuntimeOnly", libs.library("junit-platform-launcher"))
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
        }
    }
}
