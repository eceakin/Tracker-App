plugins {
    alias(libs.plugins.namaztakibi.android.library)
}

android {
    namespace = "com.eceakin.namaztakibi.widget"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
}
