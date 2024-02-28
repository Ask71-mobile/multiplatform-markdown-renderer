buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { setUrl("https://androidx.dev/storage/compose-compiler/repository") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.5")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.10")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:11.1.0")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.6.0")
    }
}

allprojects {
    group = ext.get("GROUP")!!
    version = ext.get("VERSION_NAME")!!

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { setUrl("https://androidx.dev/storage/compose-compiler/repository") }
    }
}

// subprojects {
//     apply(from = "../detekt.gradle")
//     dependencies {
//         "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
//     }
// }