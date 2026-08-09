plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.pos.offline"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.pos.offline"
        minSdk = 26
        targetSdk = 36
        
        // Ambil versionCode dari perintah Gradle (dari GitHub Actions), 
        // jika dijalankan lokal/tanpa parameter, default ke 2.
        val autoVersionCode = (project.findProperty("BUILD_VERSION_CODE") as? String)?.toIntOrNull() ?: 2
        versionCode = autoVersionCode
        
        versionName = "1.0.0.$autoVersionCode" // Version name juga akan otomatis mengikuti
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { 
        compose = true 
    }

    sourceSets {
        named("androidTest") {
            assets.directories.add("$projectDir/schemas")
        }
        named("debug") {
            assets.directories.add("$projectDir/schemas")
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.version",
                "META-INF/INDEX.LIST"
            )

            pickFirsts += setOf(
                "META-INF/services/javax.xml.stream.XMLInputFactory",
                "META-INF/services/javax.xml.stream.XMLOutputFactory",
                "META-INF/services/javax.xml.stream.XMLEventFactory",
                "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.DTD",
                "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.RELAXNG",
                "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.W3C"
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // ===== ROOM =====
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // ===== COMPOSE & UI =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    // ===== LIFECYCLE =====
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)

    // ===== PRINTER & BARCODE =====
    implementation(libs.escpos.thermalprinter)
    implementation(libs.flinger)

    // ===== CAMERA & ML KIT =====
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.google.mlkit.barcode)

    // ===== EXCEL & XML =====
    implementation(libs.dhatim.fastexcel)
    implementation(libs.dhatim.fastexcel.reader)
    implementation(libs.fasterxml.aalto.xml)
    implementation(libs.javax.stax.api)

    // ===== TESTING =====
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
