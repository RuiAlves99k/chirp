package com.ruialves.chat.data.di

import com.ruialves.chat.data.lifecycle.AppLifecycleObserver
import com.ruialves.chat.data.network.ConnectivityObserver
import com.ruialves.chat.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformChatDataModule: Module = module {
    single { DatabaseFactory() }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
}
