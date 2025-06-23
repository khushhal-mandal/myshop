plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)

    // ksp-annotation-processing
    id("com.google.devtools.ksp")
    // dagger-hilt
    id("com.google.dagger.hilt.android")
    // kotlin-serialization
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.myshoppinguserapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myshoppinguserapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // dagger-hilt-dependency-injection
    implementation("com.google.dagger:hilt-android:2.56.2")
    // dagger-hilt-annotation-processing
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    // dagger-hilt-navigation
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    // dagger-hilt-navigation-viewmodel
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // dagger-hilt-viewmodel
    ksp("androidx.hilt:hilt-compiler:1.0.0")
    // image-loader
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")
    // kotlinx-serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    // jetpack compose integration
    implementation("androidx.navigation:navigation-compose:2.9.0")
    // razor pay
    implementation ("com.razorpay:checkout:1.6.40")
    // extended icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
}