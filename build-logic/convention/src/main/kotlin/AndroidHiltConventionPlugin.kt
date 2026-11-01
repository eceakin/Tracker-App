import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

// M-07 — Hilt bu projede yalnızca KSP ile kurulur (kapt yasak). Bu plugin bir Android
// plugin'i uygulamaz; yalnızca Android application/library uygulanmış modüllerde çalışır.
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val androidApplied =
                pluginManager.hasPlugin("com.android.application") ||
                    pluginManager.hasPlugin("com.android.library")
            if (!androidApplied) {
                throw GradleException(
                    "namaztakibi.android.hilt yalnızca namaztakibi.android.application ya da " +
                        "namaztakibi.android.library uygulanmış bir modülde kullanılabilir."
                )
            }

            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            val libs = versionCatalog()
            dependencies {
                add("implementation", libs.library("hilt-android"))
                add("ksp", libs.library("hilt-compiler"))
            }
        }
    }
}
