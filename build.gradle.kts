// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// K-01 — motor sürümü eklenti sürümünden bağımsızdır; ikisi de catalog'dan gelir.
val ktlintEngineVersion = libs.versions.ktlintEngine.get()

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(ktlintEngineVersion)
    }
}

// Included build'lerin görevleri kendiliğinden çalışmaz; build-logic'i de aynı komutla denetle.
tasks.named("ktlintCheck") {
    dependsOn(gradle.includedBuild("build-logic").task(":convention:ktlintCheck"))
}

// M-08 — mimari-ve-kodlama-ilkeleri.md Bölüm 1'deki izin tablosu. Tabloya modül ya da izin
// eklemek mimari bir karardır: önce belge, sonra bu harita değişir. `null` "hepsi" demektir.
val m08AllowedDependencies: Map<String, Set<String>?> = mapOf(
    ":core:domain" to emptySet(),
    ":core:data" to setOf(":core:domain"),
    ":core:prayertimes" to setOf(":core:domain"),
    ":feature:*" to setOf(":core:domain"),
    ":widget" to setOf(":core:domain", ":core:data"),
    ":app" to null
)

fun Project.hasBuildScript(): Boolean =
    this != rootProject && layout.projectDirectory.file("build.gradle.kts").asFile.exists()

fun m08AllowedTargets(modulePath: String): Set<String>? {
    val rule = m08AllowedDependencies.keys.firstOrNull { pattern ->
        pattern == modulePath || patternIsMatch(pattern, modulePath)
    } ?: throw GradleException(
        "M-08 ihlali: $modulePath modülü M-08 izin tablosunda tanımlı değil. " +
            "Modülü kök build.gradle.kts'teki tabloya ekle."
    )
    return m08AllowedDependencies[rule]
}

fun patternIsMatch(pattern: String, modulePath: String): Boolean =
    pattern.endsWith(":*") && modulePath.startsWith(pattern.removeSuffix("*"))

fun checkM08Dependency(
    modulePath: String,
    configurationName: String,
    targetPath: String,
    allowedTargets: Set<String>
) {
    // AGP, androidTest yapılandırmalarına modülün kendisini ekler.
    if (targetPath == modulePath || targetPath in allowedTargets) return
    throw GradleException(
        "M-08 ihlali: $modulePath → $targetPath (yapılandırma: $configurationName). " +
            "İzin verilenler: ${allowedTargets.sorted()}"
    )
}

gradle.projectsEvaluated {
    val modules = allprojects.filter { it.hasBuildScript() }
    val modulePaths = modules.map { it.path }.toSet()

    modules.forEach { module ->
        val allowedTargets = m08AllowedTargets(module.path) ?: (modulePaths - module.path)
        module.configurations.forEach { configuration ->
            val projectDependencies =
                configuration.dependencies.withType(ProjectDependency::class.java)
            projectDependencies.forEach { dependency ->
                checkM08Dependency(
                    module.path,
                    configuration.name,
                    dependency.path,
                    allowedTargets
                )
            }
        }
    }
}
