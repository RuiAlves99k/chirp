plugins {
    alias(libs.plugins.convention.kmp.library)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.domain)
                implementation(libs.koin.core)
                implementation(libs.touchlab.kermit)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.security.crypto)
            }
        }

        iosMain {
            dependencies {
            }
        }
    }
}
