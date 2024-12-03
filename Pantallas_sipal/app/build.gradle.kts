plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.pantallas_sipal"
    compileSdk = 35

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }

    defaultConfig {
        applicationId = "com.example.pantallas_sipal"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures.viewBinding = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    //Implementaciones para el codigo QR
    implementation ("com.google.zxing:core:3.5.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    //Para implementar el lector decodigo Qr
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")


    // API de Google Sheets
    implementation("com.google.apis:google-api-services-sheets:v4-rev20230227-2.0.0") // prueba con esta versión

    // Biblioteca Gson para trabajar con JSON
    //implementation("com.google.code.gson:gson:2.10") //-------------->

    // Biblioteca OkHttp para solicitudes HTTP
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // Biblioteca de autenticación de Google
    implementation("com.google.auth:google-auth-library-oauth2-http:1.11.0") // última versión estable

    // Para la autenticación de Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    // Cliente API de Google para Android
    implementation("com.google.api-client:google-api-client-android:1.34.0")

}