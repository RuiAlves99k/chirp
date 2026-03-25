import com.android.build.api.dsl.LibraryExtension
import com.ruialves.chirp.convention.configureKotlinAndroid
import com.ruialves.chirp.convention.configureKotlinMultiplatform
import com.ruialves.chirp.convention.libs
import com.ruialves.chirp.convention.pathToResourcePrefix
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("com.android.library")
            }

            configureKotlinMultiplatform()

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)

                resourcePrefix =  this@with.pathToResourcePrefix()

                // Required to make debug build of app run in iOS simulator
                experimentalProperties["android.expermimental.kmp.enableAndroidResources"] = "true"

            }

            dependencies {
                "commonMainImplementation"(libs.findLibrary("kotlinx-serialization-json").get())
                "commonMainImplementation"(libs.findLibrary("kotlin-test").get())
            }
        }
    }
}