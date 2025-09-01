pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}


val storageUrl =  "https://storage.googleapis.com"
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven(uri("$storageUrl/download.flutter.io"))
//        maven(uri("/Users/bean/Documents/项目/creek_sdk_flutter/build/host/outputs/repo"))
        maven(uri("https://creekwearable.github.io/static/repo"))
    }
}

rootProject.name = "creek_dial_android"
include(":app")
 