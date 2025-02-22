plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smart_ai_sudoku_solver"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smart_ai_sudoku_solver"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    dependencies {
        implementation (libs.appcompat)
        implementation (libs.constraintlayout)
        implementation (libs.material) // Latest stable version (update if needed)
        // Other dependencies...
    }
    implementation (libs.appcompat)
    implementation (libs.constraintlayout)
    implementation (libs.material)
    implementation (libs.appcompat)
    implementation (libs.constraintlayout)
    implementation(libs.appcompat.v161)
    implementation(libs.constraintlayout.v214)
    implementation(libs.material)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}