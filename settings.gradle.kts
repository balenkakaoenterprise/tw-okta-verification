pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("kotlin", "1.9.20")
            version("compose", "1.3.1")
            version("androidCore", "1.10.1")
            version("appCompat", "1.7.0")
            version("material", "1.12.0")
            version("constraintLayout", "2.1.4")
            version("lifecycleRuntime", "2.6.1")
            version("activityCompose", "1.9.0")

            library("kotlin-stdlib", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.20")
            library("androidCore", "androidx.core:core-ktx:1.10.1")
            library("appCompat", "androidx.appcompat:appcompat:1.7.0")
            library("material", "com.google.android.material:material:1.12.0")
            library("constraintLayout", "androidx.constraintlayout:constraintlayout:2.1.4")
            library("lifecycleRuntime", "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
            library("activityCompose", "androidx.activity:activity-compose:1.9.0")
            library("composeUi", "androidx.compose.ui:ui:1.3.1")
            library("composeMaterial", "androidx.compose.material:material:1.3.1")
            library("composeUiTooling", "androidx.compose.ui:ui-tooling-preview:1.3.1")
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "twoktaand"
include(":app")
