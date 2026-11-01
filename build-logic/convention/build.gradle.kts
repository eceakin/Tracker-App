plugins {
    `kotlin-dsl`
    alias(libs.plugins.ktlint)
}

// Eklenti sürümleri ana projedekiyle aynı olsun diye catalog'dan gelir. compileOnly
// kullanılıyor: ana projenin kök `plugins { ... apply false }` blokları bu eklentileri
// derleme sınıf yoluna zaten getiriyor; implementation çakışma riski doğurur.
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
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
    }
}
