plugins {
    alias(libs.plugins.namaztakibi.android.library)
}

android {
    namespace = "com.eceakin.namaztakibi.core.prayertimes"
}

dependencies {
    implementation(project(":core:domain"))
}
