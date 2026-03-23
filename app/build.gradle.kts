import java.util.Properties
import java.io.FileInputStream

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.projectDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace   = "com.carlos.ismartshell"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.carlos.ismartshell"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyectar el token público como un campo de BuildConfig o recurso
        val mapboxPublicToken = localProperties.getProperty("MAPBOX_PUBLIC_TOKEN") ?: ""
        resValue("string", "mapbox_access_token", mapboxPublicToken)
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxPublicToken\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures { 
        compose = true 
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.json)
    implementation(libs.okhttp.logging)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coil
    implementation(libs.io.coil.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ML Kit — barcode
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Permisos
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Mapbox
    implementation("com.mapbox.maps:android:10.16.0")
    implementation("com.mapbox.extension:maps-compose:0.1.0")

    // Location (needed for user location regardless of map provider)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // QR Generation
    implementation("com.google.zxing:core:3.5.3")

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
}
