import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

extensions.configure<ApplicationExtension> {
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
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/INDEX.LIST"
            )
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
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
    
    // Ktor client dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.serialization.xml)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

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

    // Test dependencies
    testImplementation(libs.kotlintest.runner.junit5)
    testImplementation(libs.koin.test)
    testImplementation(libs.ktor.client.okhttp)
    testImplementation(libs.ktor.serialization.json)
    testImplementation(libs.kotlinx.serialization.json)

    // QR Code generation
    implementation(libs.zxing.core)

    // No-op SLF4J for Ktor (required for Android compatibility)
    implementation(libs.slf4j.nop)

    // Koin DI
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)

    // Security
    implementation(libs.security.crypto)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
}