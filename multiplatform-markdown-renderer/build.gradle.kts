plugins {
    alias(libs.plugins.myapp.android.library)
    alias(libs.plugins.myapp.android.library.compose)
}

android {
    namespace = "com.mikepenz.markdown"
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    api("org.jetbrains:markdown:0.7.3")
    
    // Use your project's Compose BOM
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
}