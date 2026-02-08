plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "top.k88936.nextcloud_tv"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "top.k88936.nextcloud_tv"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
}