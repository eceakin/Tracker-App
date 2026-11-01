plugins {
    alias(libs.plugins.namaztakibi.android.library)
}

android {
    namespace = "com.eceakin.namaztakibi.core.data"
}

dependencies {
    implementation(project(":core:domain"))
}
