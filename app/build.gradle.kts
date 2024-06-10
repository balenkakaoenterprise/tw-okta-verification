plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.tw_okta_and"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.tw_okta_and"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }

    signingConfigs {
        create("release") {
            keyAlias = "key"  // 키스토어 생성 시 설정한 key alias
            keyPassword = "12341234" // 키스토어 생성 시 설정한 key password
            storeFile = file("/Users/SHLEE/AndroidStudioProjects/twoktaand/keyy")  // 키스토어 파일 경로
            storePassword = "12341234"  // 키스토어 생성 시 설정한 key store password
        }
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    lint {
        abortOnError = false
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.1")
    implementation(libs.firebase.common.ktx)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.3")
    implementation ("com.google.firebase:firebase-analytics-ktx:21.0.0")
    implementation ("com.google.firebase:firebase-bom:28.4.0")
    implementation ("com.google.firebase:firebase-analytics:21.2.0")
    implementation ("com.google.firebase:firebase-crashlytics:18.2.9")
    implementation ("com.google.firebase:firebase-messaging:23.1.2")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("androidx.compose.ui:ui:1.0.3")
    implementation ("androidx.compose.material:material:1.0.3")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.0.3")
    debugImplementation ("androidx.compose.ui:ui-tooling:1.0.3")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    implementation ("androidx.activity:activity-compose:1.3.1")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.18.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation ("androidx.compose.runtime:runtime-livedata:1.0.5")
}

tasks.named("preBuild") {
    dependsOn("processReleaseGoogleServices")
}

apply(plugin = "com.google.gms.google-services")
apply(plugin = "com.google.firebase.crashlytics")