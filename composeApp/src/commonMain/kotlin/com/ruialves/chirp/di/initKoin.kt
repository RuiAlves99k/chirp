package com.ruialves.chirp.di

import com.ruialves.auth.presentation.di.authPresentationModule
import com.ruialves.chat.presentation.di.chatPresentationModule
import com.ruialves.core.data.di.coreDataModule
import com.ruialves.core.domain.crash.CrashReporter
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    val koinApp = startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            chatPresentationModule
        )
    }

    koinApp.koin.get<CrashReporter>().initialize()
}
