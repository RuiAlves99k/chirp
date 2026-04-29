package com.ruialves.core.domain.analytics

data class AnalyticsConfig(
    val token: String,
    val userId: String? = null,
    val serverUrl: String = DEFAULT_SERVER_URL,
) {
    companion object {
        const val DEFAULT_SERVER_URL = "https://api-eu.mixpanel.com"
    }
}
