import java.math.BigDecimal
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

// M-07 — :core:domain gibi saf Kotlin/JVM modülleri için ortak ayarlar.
// K-15 — Kapsam ölçümü ve %90 satır kapsamı kapısı yalnızca bu plugin'de kurulur;
// Android modüllerine JaCoCo eklenmez.
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("jacoco")

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

            extensions.configure<JacocoPluginExtension> {
                toolVersion = libs.version("jacoco")
            }

            tasks.named<JacocoReport>("jacocoTestReport") {
                dependsOn(tasks.named("test"))
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }
            }

            tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
                dependsOn(tasks.named("test"))
                violationRules {
                    rule {
                        limit {
                            counter = "LINE"
                            value = "COVEREDRATIO"
                            minimum = BigDecimal.valueOf(ProjectConfig.MIN_LINE_COVERAGE)
                        }
                    }
                }
            }

            tasks.named("check") {
                dependsOn(tasks.named("jacocoTestCoverageVerification"))
            }
        }
    }
}
