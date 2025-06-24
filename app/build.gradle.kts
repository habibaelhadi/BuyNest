import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.apollographql.apollo3").version("3.8.6")
}

android {
    namespace = "com.example.buynest"
    compileSdk = 35

    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(FileInputStream(file))

    defaultConfig {
        applicationId = "com.example.buynest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SHOPIFY_ACCESS_TOKEN", properties.getProperty("SHOPIFY_ACCESS_TOKEN"))
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", properties.getProperty("STRIPE_PUBLISHABLE_KEY"))
        buildConfigField("String", "STRIPE_SECRET_KEY", properties.getProperty("STRIPE_SECRET_KEY"))
        buildConfigField("String", "CRUUENCY_API_KEY", properties.getProperty("CRUUENCY_API_KEY"))
        buildConfigField("String", "PLACES_API_KEY", properties.getProperty("PLACES_API_KEY"))
        buildConfigField("String", "Admin_ACCESS_TOKEN", properties.getProperty("Admin_ACCESS_TOKEN"))
        resValue ("string", "maps_api_key", properties.getProperty("PLACES_API_KEY"))

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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //NavigationBar
    implementation (libs.curved.bottom.navigation)
    //Serialization for NavArgs
    implementation(libs.kotlinx.serialization.json)
    //NavController
    implementation(libs.androidx.navigation.compose)
    //pager for ads section
    implementation (libs.accompanist.pager.indicators)
    // Accompanist Pager
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators.v0340)
    //lottie
    implementation (libs.lottie.compose)
    //NavigationBar
    implementation (libs.curved.bottom.navigation)
    //Serialization for NavArgs
    implementation(libs.kotlinx.serialization.json)
    //NavController
    implementation(libs.androidx.navigation.compose)
    //pager for ads section
    implementation (libs.accompanist.pager.indicators)
    // Accompanist Pager
    implementation (libs.accompanist.pager)
    implementation (libs.accompanist.pager.indicators.v0340)
    //compose
    implementation(libs.androidx.material.icons.extended)
    //firebase
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation (libs.gms.play.services.auth)
    //view model
    implementation(libs.androidx.lifecycle.viewmodel.compose.android)
    // Material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Apollo-GraphQL
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.6")
    // Stripe SDK
    implementation("com.stripe:stripe-android:21.17.0")
    implementation("com.stripe:paymentsheet:21.17.0")
    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp (with logging)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    //coil for image
    implementation(libs.coil.compose)
    // EncryptedSharedPreferences
    implementation ("androidx.security:security-crypto:1.1.0-alpha03")
    // OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    // Google Places API
    implementation("com.google.android.libraries.places:places:3.4.0")
    //flow layout
    implementation (libs.accompanist.flowlayout)
    //Zoomable
    implementation("me.saket.telephoto:zoomable-image-coil:0.16.0")
    //koin
    val koin_android_version = "4.0.2"
    implementation("io.insert-koin:koin-android:$koin_android_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_android_version")
    implementation("io.insert-koin:koin-androidx-compose-navigation:$koin_android_version")
    implementation("io.insert-koin:koin-androidx-navigation:$koin_android_version")
    //koin test dependencies
    testImplementation("io.insert-koin:koin-test-junit4")
    testImplementation("io.insert-koin:koin-android-test")
}

apollo {
    service("shopify") {
        packageName.set("com.example.buynest")
        schemaFile.set(file("src/main/graphql/shopify/schema.graphqls"))
        srcDir("src/main/graphql/shopify")
    }
    service("shopify-admin") {
        packageName.set("com.example.buynest.admin")
        schemaFile.set(file("src/main/graphql/shopify-admin/schema.graphqls"))
        srcDir("src/main/graphql/shopify-admin/queries")
    }
}