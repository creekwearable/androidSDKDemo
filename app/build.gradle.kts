plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.creek.dial"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.creek.dial"
        minSdk = 24
        targetSdk = 34
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.example.creek_sdk:flutter_release:5.5")
    implementation("creek_aar:creek_aar_release:5.5")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material:material:1.6.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.protobuf:protobuf-javalite:4.0.0-rc-2")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0")

}

//configurations.all {
//    resolutionStrategy {
//        force("com.google.protobuf:protobuf-javalite:4.0.0-rc-2")
//        force("com.google.protobuf:protobuf-java:3.22.3")
//    }
//}
//dependencies {
//    implementation("com.example.creek_sdk:flutter_release:5.0") {
//        // exclude unnecessary protobuf-java
//        exclude(group = "com.google.protobuf", module = "protobuf-java")
//    }
//}
////Other libraries
//dependencies {
//    implementation("io.coil-kt:coil-compose:2.6.0") {
//        // exclude unnecessary protobuf-javalite
//        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
//    }
//}