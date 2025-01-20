plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.art"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.art"
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.paging.common.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // main dependency


    implementation(libs.androidx.navigation.compose)

    implementation(libs.coil.compose)


    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.migration)
    ksp(libs.androidx.room.compiler)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.lottie.compose)

    implementation(libs.coil.compose.v200)

    implementation(libs.accompanist.swiperefresh) // for the swip update list of posts


    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.runtime.livedata)
    debugImplementation(libs.ui.tooling)
    implementation(libs.material3)

    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.activity.compose.v190)
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.compose.material.material)

    implementation(libs.androidx.navigation.compose.v277)

    implementation(libs.androidx.paging.runtime.ktx) // For Paging 3
    implementation(libs.androidx.paging.compose) // For Paging with Compose
    implementation(libs.androidx.paging.common.android)
    implementation(libs.androidx.paging.rxjava2)
    implementation(libs.androidx.paging.rxjava3)


    implementation(libs.androidx.ui.v1xx) // For Jetpack Compose
    implementation(libs.androidx.material.v1xx) // For Material components


    // okhttp
    implementation(libs.okhttp3.okhttp.v493)
    implementation(libs.okhttp3.logging.interceptor.v493)
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json.v130)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.converter.gson)

    implementation(libs.material)


    implementation(libs.ui) // Core UI components
    implementation(libs.ui.tooling.preview) // Preview support
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle support


    implementation("com.google.android.gms:play-services-auth:20.4.0")

    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.kotlinx.datetime)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)


    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    // lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation("androidx.room:room-runtime:2.4.0")
    implementation(libs.androidx.room.ktx) // Room KTX library
    implementation(libs.androidx.room.migration) // Room migration library

}