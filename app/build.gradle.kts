@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp").version("1.8.10-1.0.9")

    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rolandsarosy.chatfeedchallenge"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"
        resourceConfigurations.addAll(listOf("en"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    namespace = "com.rolandsarosy.chatfeedchallenge"
    flavorDimensions += "environment"

    // Currently both product flavors are the same, but this showcases how they can be used to differentiate the "two applications", in a way
    // That they're acting as two different apps with different icons, names, instances on a phone and API keys.
    productFlavors {
        create("production") {
            dimension = "environment"
            versionNameSuffix = ".production"
            applicationIdSuffix = ".production"

            resValue("string", "app_name", "Chat Feed Challenge")
            buildConfigField("String", "BASE_URL", "\"https://dummyjson.com/products\"")
        }

        create("staging") {
            dimension = "environment"
            versionNameSuffix = ".staging"
            applicationIdSuffix = ".staging"

            resValue("string", "app_name", "Chat Feed Challenge Staging")
            buildConfigField("String", "BASE_URL", "\"https://dummyjson.com/products\"")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // AppCompat
    val appCompatVersion by extra("1.6.1")
    implementation("androidx.appcompat:appcompat:$appCompatVersion")

    // KTX Core
    val ktxCoreVersion by extra("1.10.0")
    implementation("androidx.core:core-ktx:$ktxCoreVersion")

    // ConstraintLayout
    val constraintLayoutVersion by extra("2.1.4")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")

    // Android material components
    val materialVersion by extra("1.8.0")
    implementation("com.google.android.material:material:$materialVersion")
}
