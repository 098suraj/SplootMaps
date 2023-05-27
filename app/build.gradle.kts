import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    id("org.jmailen.kotlinter")
    id("kotlin-android")
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mapsplatform.secrets)
    id("org.jetbrains.kotlin.kapt")

}

android {
    namespace = "com.example.template"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.template"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
        kapt {
            correctErrorTypes = true
        }

    }



    buildTypes {
        getByName("debug") {

        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {

        compose = true
        dataBinding = true
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"

    }


}

dependencies {

    //permission
    implementation(libs.accompanist.permissions)
    //serialization
    implementation(libs.kotlin.serialization.json)
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.lifecycle.runtime.compose)
    //navigation
    implementation(libs.androidx.navigation.compose)
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt and instrumented tests.
//    androidTestImplementation(libs.hilt.android.testing)
//    kaptAndroidTest(libs.hilt.android.compiler)
    // Hilt and Robolectric tests.
//    testImplementation(libs.hilt.android.testing)
//    kaptTest(libs.hilt.android.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    debugImplementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    //networking
    implementation(libs.com.squareup.retrofit2)
    implementation(libs.com.squareup.okhttp3.interceptor)
    implementation(libs.com.squareup.retrofit2.gson)
    implementation(libs.com.jakewharton.timber)

    //maps
    implementation(libs.com.google.maps)
    implementation(libs.com.google.android.gms)
    implementation(libs.play.services.location)
    implementation(libs.com.google.android.libraries.places)

    // coil
    implementation(libs.io.coil.kt.coil)
    implementation(libs.io.coil.kt.coil.compose)
    //kotlinter and deket

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Instrumented tests
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Local tests: jUnit, coroutines, Android runner
//    testImplementation(libs.junit)
//    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests: jUnit rules and runners
//    androidTestImplementation(composeBom)
//    androidTestImplementation(libs.androidx.test.core)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.androidx.test.runner)
}
