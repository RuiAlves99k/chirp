import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.ruialves.chirp.convention.configureKotlinAndroid
import com.ruialves.chirp.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(this.pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                namespace = "com.ruialves.chirp"
                compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()

                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()

                    val gradleLocalProperties = gradleLocalProperties(rootDir, providers)
                    val baseUrl = gradleLocalProperties.getProperty("BASE_URL")
                        ?: throw IllegalArgumentException("Missing BASE_URL in the properties")
                    manifestPlaceholders["deepLinkHost"] = baseUrl
                }

                flavorDimensions += "environment"
                productFlavors {
                    create("alpha") {
                        dimension = "environment"
                        applicationIdSuffix = ".alpha"
                    }
                    create("prod") {
                        dimension = "environment"
                    }
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }

                configureKotlinAndroid(this)
            }
        }
    }
}
