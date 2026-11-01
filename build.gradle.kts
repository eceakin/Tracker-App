// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

// Included build'lerin görevleri kendiliğinden çalışmaz; build-logic'i de aynı komutla denetle.
tasks.named("ktlintCheck") {
    dependsOn(gradle.includedBuild("build-logic").task(":convention:ktlintCheck"))
}
