package com.ruialves.core.domain.analytics

interface AnalyticsAdapter {
    fun initialize(config: AnalyticsConfig)
    fun trackEvent(event: String, properties: Map<String, Any> = emptyMap())
    fun identifyUser(userId: String)
    fun reset()
    fun flush()
}
