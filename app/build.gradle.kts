plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.msoe.drobeka.jpdonsite"
    compileSdk = 34

    packagingOptions {
        resources.excludes.apply {
            add("META-INF/DEPENDENCIES")
            add("META-INF/*.properties")
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }

    defaultConfig {
        applicationId = "edu.msoe.drobeka.jpdonsite"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
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
        viewBinding = true
    }
}

dependencies {
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.api-client:google-api-client-android:1.23.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    kapt("androidx.room:room-compiler:2.5.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}