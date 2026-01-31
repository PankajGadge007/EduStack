plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.pankajgadge.edustack"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pankajgadge.edustack"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }


    flavorDimensions += "backend"
    productFlavors {
        create("firebase") {
            dimension = "backend"
            buildConfigField("Boolean", "USE_FIREBASE", "true")
        }
        create("retrofit") {
            dimension = "backend"
            buildConfigField("Boolean", "USE_FIREBASE", "false")
        }
    }

}

dependencies {
    // Module dependencies
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:quiz"))
    implementation(project(":feature:practical"))
    implementation(project(":feature:help"))

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Firebase
    implementation(platform(libs.firebase.bom))  // Firebase BOM (Bill of Materials) manages all Firebase library versions
    implementation(libs.bundles.firebase)         // All Firebase libraries (Includes: Firestore, Auth, Storage)
//    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler.ksp)
    implementation(libs.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Core
    implementation(libs.androidx.core.ktx)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

}
