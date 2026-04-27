import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import com.ruialves.chirp.convention.pathToPackageName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.Actions.with
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }

            extensions.configure<BuildKonfigExtension> {
                packageName = target.pathToPackageName()

                val flavor = detectFlavor(target)

                defaultConfigs {
                    val gradleLocalProperties = gradleLocalProperties(rootDir, rootProject.providers)
                    val apiKey = gradleLocalProperties.getProperty("API_KEY") ?: throw IllegalStateException(
                        "Missing API_KEY property in local.properties"
                    )
                    buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)

                    val baseUrl = gradleLocalProperties.getProperty("BASE_URL") ?: throw IllegalStateException(
                        "Missing BASE_URL property in local.properties"
                    )
                    buildConfigField(FieldSpec.Type.STRING, "BASE_URL", baseUrl)

                    val isAlpha = flavor == "alpha"
                    buildConfigField(FieldSpec.Type.BOOLEAN, "IS_DEBUG", isAlpha.toString())
                    buildConfigField(FieldSpec.Type.STRING, "APP_ID_SUFFIX", if (isAlpha) ".alpha" else "")
                    buildConfigField(FieldSpec.Type.STRING, "FLAVOR_NAME", flavor)
                }
            }
        }
    }

    private fun detectFlavor(project: Project): String {
        return project.rootProject.findProperty("flavor")?.toString()
            ?: project.providers.gradleProperty("flavor").orNull
            ?: detectFlavorFromTaskRequests(project)
            ?: "prod"
    }

    private fun detectFlavorFromTaskRequests(project: Project): String? {
        val taskRequests = project.gradle.startParameter.taskRequests
            .flatMap { it.args }
            .map { it.lowercase() }
        return when {
            taskRequests.any { it.contains("alpha") } -> "alpha"
            taskRequests.any { it.contains("prod") } -> "prod"
            else -> null
        }
    }
}
