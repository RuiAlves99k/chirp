plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.koin.core)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
            }
        }
    }
}
