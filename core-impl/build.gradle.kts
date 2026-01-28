plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pankajgadge.core.impl"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {

    implementation(project(":core-api"))

    // Hilt (KSP)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler.ksp)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // JSON
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.ksp)

    // Logging
    implementation(libs.timber)

    implementation(libs.androidx.core.ktx)
}
