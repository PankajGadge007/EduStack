plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.compose)
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
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

//    kotlin {
//        jvmToolchain(17)
//    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    // Modules
    implementation(project(":core:api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:firebase"))
    implementation(project(":core:ui"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:quiz"))
    implementation(project(":feature:practical"))
    implementation(project(":feature:help"))
    implementation(project(":security"))
    implementation(project(":designsystem"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.appcompat)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Logging
    implementation(libs.timber)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
//    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler.ksp)
    implementation(libs.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
