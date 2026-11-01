plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
}

// K-01 — build-logic de aynı motor sürümüyle denetlenir.
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(libs.versions.ktlintEngine.get())
}

// Eklenti sürümleri ana projedekiyle aynı olsun diye catalog'dan gelir. compileOnly
// kullanılıyor: ana projenin kök `plugins { ... apply false }` blokları bu eklentileri
// derleme sınıf yoluna zaten getiriyor; implementation çakışma riski doğurur.
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("jvmLibrary") {
            id = "namaztakibi.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidLibrary") {
            id = "namaztakibi.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "namaztakibi.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidApplication") {
            id = "namaztakibi.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidHilt") {
            id = "namaztakibi.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}
