package com.ruialves.chirp.di

import com.ruialves.core.domain.analytics.AnalyticsAdapter

fun initKoin(
    analyticsAdapter: AnalyticsAdapter? = null,
) {
    initKoin(
        analyticsAdapter,
        config = null
    )
}
