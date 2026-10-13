import org.gradle.api.JavaVersion
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

// ZEGOCLOUD credentials are not committed to version control.
// Create local.properties (gitignored) with: zegoAppId=<id> and zegoAppSign=<sign>
// See README.md for how to find them in the ZEGOCLOUD console.
val localProperties = java.util.Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val zegoAppId: String = localProperties.getProperty("zegoAppId")
    ?: (project.findProperty("zegoAppId") as String?)
    ?: "0"
val zegoAppSign: String = localProperties.getProperty("zegoAppSign")
    ?: (project.findProperty("zegoAppSign") as String?)
    ?: ""


android {
    namespace = "com.example.chatter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatter"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("long", "ZEGO_APP_ID", "${zegoAppId}L")
        buildConfigField("String", "ZEGO_APP_SIGN", "\"$zegoAppSign\"")
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
        isCoreLibraryDesugaringEnabled = true

    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        dataBinding = true
        // Required for the ZEGO credentials above: AGP 8 does not generate BuildConfig
        // unless you ask for it.
        buildConfig = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation(libs.dagger.hilt.android)
    implementation(libs.dagger.hilt.compose)
    implementation(libs.coil)
    implementation(platform(libs.firebase.bom)) // Firebase BOM handles versioning
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material.v190)
    implementation  (libs.gson)

    // Pinned deliberately: a "+" version resolves to whatever the newest release is on
    // the day you build, so two checkouts of the same commit can compile differently.
    implementation("com.github.ZEGOCLOUD:zego_inapp_chat_uikit_android:2.7.6")  {
        exclude(group = "com.android.support", module = "support-compat")}


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    kapt(libs.dagger.hilt.compiler)
    implementation (libs.zego.uikit.prebuilt.call.android)

    implementation(libs.permissionx)
//    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:1.0.0")

    implementation (libs.material)
    implementation  (libs.androidx.material.icons.extended)
    implementation(libs.androidx.espresso.core)
// new
    implementation  (libs.firebase.firestore.ktx.v2491)


}
