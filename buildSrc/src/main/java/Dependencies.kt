object Versions {
    const val androidMinSdk = 21
    const val androidCompileSdk = 34
    const val androidTargetSdk = androidCompileSdk

    const val kotlin = "1.9.23"

    const val markdown = "0.7.0"

    const val coil = "2.6.0"
    const val compose = "1.6.6"
    const val composeCompiler = "1.5.12"

    const val material = "1.11.0"
    const val activityCompose = "1.9.0"
}

object Deps {
    object Android {
        const val material = "com.google.android.material:material:${Versions.material}"
    }

    object AndroidX {
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    }

    object Markdown {
        const val core = "org.jetbrains:markdown:${Versions.markdown}"
    }

    object Compose {
        const val ui = "androidx.compose.ui:ui:${Versions.compose}"
        const val uiGraphics = "androidx.compose.ui:ui-graphics:${Versions.compose}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val foundationLayout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"

        const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"
        const val coilComposeSvg = "io.coil-kt:coil-svg:${Versions.coil}"
    }
}
