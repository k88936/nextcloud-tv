plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "top.k88936.nextcloud_tv"
    compileSdk = 36

    defaultConfig {
        applicationId = "top.k88936.nextcloud_tv"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.leanback)
    implementation(libs.glide) {
        exclude(group = "androidx.vectordrawable", module = "vectordrawable")
    }
    implementation(libs.nextcloud.android.library) {
        exclude(group = "org.ogce", module = "xpp3") // unused in Android and brings wrong Junit version
    }
    
    // Image loading
    implementation(libs.coil.compose)
    
    // Compose BOM
    implementation(platform(libs.compose.bom))
    
    // Core Compose dependencies
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    
    // Compose for TV
    implementation(libs.compose.tv.foundation)
    implementation(libs.compose.tv.material)
    
    // Navigation
    implementation(libs.compose.navigation)

    // Debug dependencies
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}