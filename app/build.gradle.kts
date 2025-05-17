plugins {
    alias(libs.plugins.android.application) version "8.6.1"
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.art"
    compileSdk = 35

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


    implementation(libs.play.services.auth)

    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.kotlinx.datetime)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)


    // navigation
    implementation(libs.navigation.compose.v277)
    // lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Room KTX library
    implementation(libs.androidx.room.migration) // Room migration library

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)


}