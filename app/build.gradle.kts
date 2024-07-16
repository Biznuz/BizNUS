
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.biznus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.biznus"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.annotation)
    implementation(libs.firebase.database)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.rengwuxian.materialedittext:library:2.1.4")
    implementation(libs.activity)
    implementation("com.github.bumptech.glide:glide:4.8.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation("com.stripe:stripe-java:26.0.0")
    implementation("com.stripe:stripe-android:20.48.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.okhttp3:okhttp:3.2.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}