import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.ruialves.convention.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "com.ruialves.convention.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidComposeApplication") {
            id = "com.ruialves.convention.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("CmpApplication") {
            id = "com.ruialves.convention.cmp.application"
            implementationClass = "CmpApplicationConventionPlugin"
        }

        register("KmpLibrary") {
            id = "com.ruialves.convention.kmp.library"
            implementationClass = "KmpLibraryConventionPlugin"
        }

        register("CmpLibrary") {
            id = "com.ruialves.convention.cmp.library"
            implementationClass = "CmpLibraryConventionPlugin"
        }

        register("CmpFeature") {
            id = "com.ruialves.convention.cmp.feature"
            implementationClass = "CmpFeatureConventionPlugin"
        }

        register("BuildKonfig") {
            id = "com.ruialves.convention.buildkonfig"
            implementationClass = "BuildKonfigConventionPlugin"
        }


    }
}