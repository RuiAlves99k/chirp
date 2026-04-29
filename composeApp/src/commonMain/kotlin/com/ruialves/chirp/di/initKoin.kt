package com.ruialves.chirp.di

import com.ruialves.auth.presentation.di.authPresentationModule
import com.ruialves.chat.presentation.di.chatPresentationModule
import com.ruialves.core.data.di.coreDataModule
import com.ruialves.core.data.logging.KermitLoggerBackend
import com.ruialves.core.domain.analytics.AnalyticsAdapter
import com.ruialves.core.domain.analytics.AnalyticsConfig
import com.ruialves.core.domain.crash.CrashReporter
import com.ruialves.core.domain.logging.ChirpLogger
import com.ruialves.core.presentation.di.corePresentationModule
import com.ruialves.libs.identification.DeviceIdentification
import com.ruialves.libs.identification.di.identificationModule
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    analyticsAdapter: AnalyticsAdapter? = null,
    config: KoinAppDeclaration? = null,
) {
    val koinApp = startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            identificationModule,
            authPresentationModule,
            appModule,
            chatPresentationModule,
            corePresentationModule
        )
        if (analyticsAdapter != null) {
            modules(module { single<AnalyticsAdapter> { analyticsAdapter } })
        }
    }

    val crashReporter = koinApp.koin.get<CrashReporter>()
    crashReporter.initialize()

    ChirpLogger.install(KermitLoggerBackend(crashReporter = crashReporter))

    val deviceId = koinApp.koin.get<DeviceIdentification>().getDeviceId()
    val analytics = koinApp.koin.get<AnalyticsAdapter>()
    analytics.initialize(
        config = AnalyticsConfig(
            token = koinApp.koin.get<String>(named("mixpanelToken")),
            userId = deviceId
        )
    )
    analytics.flush()
}
