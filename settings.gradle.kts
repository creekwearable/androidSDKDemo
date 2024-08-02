pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}


val storageUrl =  "https://storage.googleapis.com"
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(uri("$storageUrl/download.flutter.io"))
        maven(uri("https://creekwearable.github.io/static/repo"))
    }
}

rootProject.name = "creek_dial_android"
include(":app")
 