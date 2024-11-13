import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.compose.compiler)

    id("kotlin-parcelize")
}

android {
    namespace = "es.jvbabi.vplanplus"
    compileSdk = 34

    defaultConfig {
        applicationId = "es.jvbabi.vplanplus"
        minSdk = 26
        targetSdk = 35
        versionCode = 323
        versionName = "VPP_${LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd"))}-android-insider1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "VPP_CLIENT_ID", localProperties["vpp.client_id"].toString())
        buildConfigField("String", "VPP_CLIENT_SECRET", localProperties["vpp.client_secret"].toString())
        buildConfigField("String", "VPP_REDIRECT_URI", localProperties["vpp.redirect_uri"].toString())
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }

        }

        create("stage-minify") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }

        create("stage") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(libs.balloon.compose)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.activity.compose)
    implementation(libs.animation.graphics)
    implementation(libs.material.icons.extended)
    implementation(libs.material3.android)

    implementation(libs.material)

    implementation(libs.browser)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // splash screen
    implementation(libs.core.splashscreen)

    // camera
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.core)

    // parsing
    implementation(libs.gson)
    implementation(libs.simple.xml)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.compose.qr.code)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    testImplementation(libs.junit)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)

    implementation(libs.work.runtime.ktx)

    implementation(libs.biometric)

    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.play.services.mlkit.document.scanner)
    implementation(libs.coil.compose)

    // Local unit tests
    testImplementation(libs.test.core)
    testImplementation(libs.junit)
    testImplementation(libs.core.testing)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.google.truth)
    testImplementation(libs.okhttp3.mockwebserver)
    debugImplementation(libs.ui.test.manifest)

    // Instrumentation tests
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.android.compiler)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)
    androidTestImplementation(libs.core.testing)
    androidTestImplementation(libs.google.truth)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.test.core.ktx)
    androidTestImplementation(libs.okhttp3.mockwebserver)
    androidTestImplementation(libs.runner)

    implementation(libs.compose.html)
}
