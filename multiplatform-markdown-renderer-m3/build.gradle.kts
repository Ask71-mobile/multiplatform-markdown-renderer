plugins {
    alias(libs.plugins.myapp.android.library)
    alias(libs.plugins.myapp.android.library.compose)
}

android {
    namespace = "com.mikepenz.markdown.m3"
}

dependencies {
    api(project(":multiplatform-markdown-renderer:multiplatform-markdown-renderer"))
    api("org.jetbrains:markdown:0.7.3")
    
    // Use your project's Compose BOM
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
}