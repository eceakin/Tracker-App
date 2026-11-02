import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// M-07 — SDK ve JVM değerlerinin tek kaynağı. Modül build dosyaları bu değerleri yazmaz.
object ProjectConfig {
    const val COMPILE_SDK = 37
    const val COMPILE_SDK_MINOR = 1
    const val MIN_SDK = 26
    const val TARGET_SDK = 37
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_17
    val JVM_TARGET: JvmTarget = JvmTarget.JVM_17

    // NFR-MNT-01, mimari K-15 — :core:domain satır kapsamı en az %90.
    const val MIN_LINE_COVERAGE = 0.90
}
