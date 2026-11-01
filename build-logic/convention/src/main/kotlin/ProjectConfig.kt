import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// M-07 — SDK ve JVM değerlerinin tek kaynağı. Modül build dosyaları bu değerleri yazmaz.
object ProjectConfig {
    const val COMPILE_SDK = 36
    const val COMPILE_SDK_MINOR = 1
    const val MIN_SDK = 26
    const val TARGET_SDK = 36
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_11
    val JVM_TARGET: JvmTarget = JvmTarget.JVM_11
}

// K-06 — convention sınıfları `libs.` erişimini kullanamaz; catalog'a tek yerden bakılır.
internal fun Project.versionCatalog(): VersionCatalog =
    extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).get()
