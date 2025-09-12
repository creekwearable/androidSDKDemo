plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.creek.dial"
    compileSdk = 34

    packaging {
        dex {
            useLegacyPackaging = true
        }
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    defaultConfig {
        applicationId = "com.creek.dial"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.14"
//    }
}



dependencies {


//    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
//    implementation("com.google.firebase:firebase-analytics")
//    implementation("com.google.firebase:firebase-inappmessaging-display")
//    implementation("com.google.firebase:firebase-installations")
//    implementation("com.google.firebase:firebase-messaging")
//    implementation("com.google.firebase:firebase-crashlytics")
//    implementation("com.google.firebase:firebase-config")
//    implementation("com.google.firebase:firebase-perf")

    implementation("com.example.creek_sdk:flutter_release:8.1")
    implementation("creek_aar:creek_aar_release:8.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    // Compose BOM 管理版本
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.0")

    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("com.google.code.gson:gson:2.8.8")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation("com.github.yalantis:ucrop:2.2.8-native")
    implementation("arthenica:creek-ffmpeg-kit-https:1.0.1")
    ///为了兼容24的API 需要指定azure-core的版本
//    implementation("com.microsoft.cognitiveservices.speech:client-sdk:1.44.0") {
//        exclude(group = "com.azure", module = "azure-core")
//    }
//    implementation("com.azure:azure-core:1.29.0")

}

//configurations.all {
//    resolutionStrategy.dependencySubstitution {
//        substitute(module("com.google.protobuf:protobuf-javalite"))
//            .using(module("com.google.protobuf:protobuf-java:3.15.0"))
//    }
//}
