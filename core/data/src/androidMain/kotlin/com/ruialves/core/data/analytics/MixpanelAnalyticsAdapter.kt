package com.ruialves.core.data.analytics

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.ruialves.core.data.BuildKonfig
import com.ruialves.core.domain.analytics.AnalyticsAdapter
import com.ruialves.core.domain.analytics.AnalyticsConfig
import com.ruialves.core.domain.logging.ChirpLogger
import org.json.JSONObject

class MixpanelAnalyticsAdapter(
    private val context: Context,
) : AnalyticsAdapter {

    private var mixpanel: MixpanelAPI? = null

    override fun initialize(config: AnalyticsConfig) {
        if (config.token.isBlank()) {
            ChirpLogger(TAG).d { "Skipping initialization — token is blank" }
            return
        }

        mixpanel = MixpanelAPI.getInstance(context, config.token, false).also { mp ->
            mp.setServerURL(config.serverUrl)
            mp.registerSuperProperties(
                JSONObject().apply {
                    put("platform", "android")
                    put("environment", BuildKonfig.FLAVOR_NAME)
                }
            )
        }

        ChirpLogger(TAG).d { "Initialized with environment=${BuildKonfig.FLAVOR_NAME}" }

        config.userId?.let { identifyUser(it) }
    }

    override fun trackEvent(event: String, properties: Map<String, Any>) {
        ChirpLogger(TAG).d { "trackEvent: $event, properties: $properties" }
        val jsonProps = if (properties.isNotEmpty()) {
            JSONObject().apply {
                properties.forEach { (key, value) -> put(key, value) }
            }
        } else {
            null
        }
        mixpanel?.track(event, jsonProps)
    }

    override fun identifyUser(userId: String) {
        ChirpLogger(TAG).d { "identifyUser: $userId" }
        mixpanel?.identify(userId)
    }

    override fun reset() {
        ChirpLogger(TAG).d { "reset" }
        mixpanel?.reset()
    }

    override fun flush() {
        mixpanel?.flush()
    }

    companion object {
        private const val TAG = "Analytics"
    }
}
