plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.stability.analyzer)
}

room {
    // Perbaikan 1: Gunakan layout.projectDirectory agar pelacakan input/output cache 9.6.1 lebih presisi
    schemaDirectory(layout.projectDirectory.dir("schemas").toString())
}

android {
    namespace = "com.pos.offline"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.pos.offline"
        minSdk = 26
        targetSdk = 37
        
        val buildNumberProvider = providers.environmentVariable("ANDROID_BUILD_NUMBER")
        val autoVersionCode = buildNumberProvider.orNull?.toIntOrNull() ?: 2
        
        versionCode = autoVersionCode
        versionName = "1.0.0.$autoVersionCode"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
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
            ndk {
            debugSymbolLevel = "NONE"
            }
        }
    }

    // Perbaikan 3: Menggunakan Java Toolchain (Sinkron dengan target kompilasi Kotlin & Java)
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
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
            assets.directories.add(layout.projectDirectory.dir("schemas").toString())
        }
        named("debug") {
            assets.directories.add(layout.projectDirectory.dir("schemas").toString())
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

    // ===== OPTIMALISASI BUNDLE (Menggunakan format tanda titik) =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui) // "compose-ui" otomatis jadi "compose.ui"
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle.runtime) // "lifecycle-runtime" otomatis jadi "lifecycle.runtime"
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.bundles.camera)
    implementation(libs.bundles.excel.parser) // "excel-parser" otomatis jadi "excel.parser"

    // ===== PRINTER & BARCODE =====
    implementation(libs.escpos.thermalprinter)
    implementation(libs.flinger)
    implementation(libs.google.mlkit.barcode)

    // ===== TESTING =====
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    lintChecks(libs.slack.compose.lints)
}

composeCompiler {
    // Arahkan ke file yang baru saja kita buat di root project
    stabilityConfigurationFile = rootProject.file("compose_compiler_config.conf")
}