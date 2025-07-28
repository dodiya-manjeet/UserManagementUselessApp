plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google Services plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.usermanagementapp_2023eb03183" // Change to your actual package name
    compileSdk = 34 // You can use 34 or 35

    defaultConfig {
        applicationId = "com.example.usermanagementapp_2023eb03183" // Change to your actual package name
        minSdk = 24
        targetSdk = 34 // You can use 34 or 35
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
    // Enable View Binding instead of Compose
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Firebase Bill of Materials (BoM) - manages library versions
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Required Firebase services for this project
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    // Required Android UI components for XML layouts
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")


    // Standard testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}