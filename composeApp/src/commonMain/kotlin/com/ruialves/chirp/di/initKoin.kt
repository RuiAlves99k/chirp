package com.ruialves.chirp.di

import com.ruialves.auth.presentation.di.authPresentationModule
import com.ruialves.chat.presentation.di.chatPresentationModule
import com.ruialves.core.data.di.coreDataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            chatPresentationModule
        )
    }
}
