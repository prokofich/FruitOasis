
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.orchardoasis"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.orchardoasis"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.onesignal:OneSignal:[4.0.0, 4.99.99]") // OneSignal
    implementation("com.amplitude:android-sdk:2.23.2") // Amplitude
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // OkHttp

    implementation(platform("com.google.firebase:firebase-bom:32.7.0")) // Firebase
    implementation("com.google.firebase:firebase-analytics") // Firebase
    implementation("com.google.firebase:firebase-firestore-ktx") // Firestore

    implementation("com.facebook.android:facebook-android-sdk:7.1.0") // Facebook

    implementation("io.coil-kt:coil:2.5.0") // картинки

    implementation("com.android.installreferrer:installreferrer:2.2")

}