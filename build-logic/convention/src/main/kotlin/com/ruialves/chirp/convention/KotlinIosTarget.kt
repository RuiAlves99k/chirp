package com.ruialves.chirp.convention

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureIosTarget() {
    extensions.configure<KotlinMultiplatformExtension> {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }
    }

    generateIosLocalProperties()
}

private fun Project.generateIosLocalProperties() {
    val gradleLocalProperties = gradleLocalProperties(rootDir, providers)
    val baseUrl = gradleLocalProperties.getProperty("BASE_URL")
        ?: throw IllegalArgumentException("Missing BASE_URL in local.properties")

    val generated = rootProject.file("iosApp/Configuration/LocalProperties.xcconfig")
    generated.writeText(
        """
        |// Generated from local.properties — do not edit manually
        |DEEP_LINK_HOST = $baseUrl
        """.trimMargin() + "\n"
    )
}