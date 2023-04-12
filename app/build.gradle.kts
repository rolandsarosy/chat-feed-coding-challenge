@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp").version("1.8.10-1.0.9")
    id("io.gitlab.arturbosch.detekt").version("1.20.0")

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

    // Navigation components
    val navComponentsVersion by extra("2.5.3")
    implementation("androidx.navigation:navigation-fragment-ktx:$navComponentsVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navComponentsVersion")

    // Lifecycle extensions
    val lifecycleExtensionsVersion by extra("2.2.0")
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleExtensionsVersion")

    // Lifecycle extensions - KTX extension
    val lifecycleExtensionsKtxVersion by extra("2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleExtensionsKtxVersion")

    // Livedata
    val liveDataVersion by extra("2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:$liveDataVersion")

    // Koin dependency injection
    val koinVersion by extra("3.4.0")
    implementation("io.insert-koin:koin-android:$koinVersion")

    // RecyclerView
    val recyclerViewVersion by extra("1.3.0")
    implementation("androidx.recyclerview:recyclerview:$recyclerViewVersion")

    // Retrofit
    val retrofitVersion by extra("2.9.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    // Okhttp 3
    val okhttpLoggingVersion by extra("5.0.0-alpha.7")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpLoggingVersion")

    // Moshi
    val moshiVersion by extra("1.14.0")
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // Coroutines
    val coroutinesVersion by extra("1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Timber logger
    val timberVersion by extra("5.0.1")
    implementation("com.jakewharton.timber:timber:$timberVersion")
}

tasks.preBuild.dependsOn(tasks.detekt)
tasks.check.dependsOn(tasks.detekt)
tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt-report.html"))
        txt.required.set(false)
        xml.required.set(false)
        sarif.required.set(false)
    }
}

detekt {
    toolVersion = "1.20.0"
    source = files("src/main/java", "src/test/java")
    parallel = true
    config = files("detekt-config.yml")
    buildUponDefaultConfig = false
    allRules = false
    disableDefaultRuleSets = false
    debug = false
    ignoreFailures = false
    ignoredBuildTypes = listOf()
    ignoredFlavors = listOf()
    ignoredVariants = listOf()
}
