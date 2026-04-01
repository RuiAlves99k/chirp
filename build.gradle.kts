plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

// Exclude Compose resource generator and BuildKonfig outputs from ktlint.
// These are third-party tool outputs that use non-standard formatting and cannot be corrected.
subprojects {
    plugins.withId("org.jlleitschuh.gradle.ktlint") {
        configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
            filter {
                exclude { element ->
                    val path = element.file.absolutePath
                    path.contains("/build/generated/compose/") ||
                        path.contains("/build/buildkonfig/")
                }
            }
        }
    }
}
}