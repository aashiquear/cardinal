import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.cardinal.car"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-common"))
    implementation(project(":core:core-data"))
    implementation(project(":feature:feature-map"))
    implementation(project(":feature:feature-routing"))
    implementation(project(":feature:feature-navigation"))
    implementation(project(":feature:feature-poi"))
    implementation(project(":feature:feature-weather"))
    implementation(project(":feature:feature-traffic"))

    implementation(libs.car.app)
    implementation(libs.car.app.projected)
    implementation(libs.maplibre.sdk)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
