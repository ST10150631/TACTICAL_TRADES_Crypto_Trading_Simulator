import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("kapt")
}

android {
    namespace = "za.co.varsitycollege.opsc7312_poe_tactical_trades"
    compileSdk = 34

    defaultConfig {
        applicationId = "za.co.varsitycollege.opsc7312_poe_tactical_trades"
        minSdk = 27
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
    buildFeatures {
        viewBinding = true
    }
}



dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.play.services.fido)
    //implementation(libs.androidx.biometric.ktx)
    testImplementation(libs.junit)
    implementation(libs.gson)
    implementation(libs.okhttp)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.glide)
    //implementation(libs.biometric)
    implementation("androidx.biometric:biometric:1.1.0")
    kapt(libs.compiler)
    //charts
    implementation("com.diogobernardino:williamchart:3.10.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Mockito for instrumented tests
    androidTestImplementation("org.mockito:mockito-android:5.2.0")
    // JUnit for running unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.robolectric:robolectric:4.9.1") // Check for the latest version

    // Mockito Core (for mocking in JVM unit tests)
    testImplementation("org.mockito:mockito-core:5.2.0")

    // Optional: Mockito Kotlin support
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0'")

    // If you want to use mockito-inline for final classes or methods
    testImplementation("org.mockito:mockito-inline:5.2.0")

    // Optional: Add Hamcrest for more expressive assertions (Mockito uses this)
    testImplementation("org.hamcrest:hamcrest-library:2.2")

    // If using Coroutines, you might need a library to support testing coroutines with Mockito
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // Mockito dependencies
    testImplementation ("org.mockito:mockito-core:4.1.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0") // Add this for Kotlin support
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    // Espresso and other testing dependencies
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")


}


apply(plugin = "com.google.gms.google-services")
