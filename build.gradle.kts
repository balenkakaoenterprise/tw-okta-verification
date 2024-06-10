plugins {
    id("com.android.application") version "8.0.0" apply false
    id("com.android.library") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // id("com.google.gms.google-services") version "4.3.10" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.10") // Firebase 추가
    }
}

allprojects {
    ext["compose_version"] = "1.4.3"
    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }
}

