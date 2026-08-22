plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.pos.offline"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.pos.offline"
        minSdk = 26 // Android 8.0 (Ideal untuk library ESCPOS & CameraX)
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    androidResources {
        localeFilters += listOf("id", "en")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        compose = true
    }
    
    // Konfigurasi Room Auto-Migration Schema
    room3 {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // 1. AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // 2. Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // 3. Navigation & Pagination UI
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.paging.compose)

    // 4. Room 3.0.1 & SQLite Bundled 
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
    implementation(libs.sqlite.bundled)

    // 5. Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // 6. Dependency Injection (Koin)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.serialization.json)

    // 7. Hardware: Printer Termal
    implementation(libs.escpos.printer)

    // 8. Hardware: Pemindai Barcode (CameraX + MLKit)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.mlkit.barcode)

    // 9. Laporan Ekspor/Impor (FastExcel & Stax)
    implementation(libs.fastexcel)
    implementation(libs.aalto.xml)
    implementation(libs.stax.api)
}
