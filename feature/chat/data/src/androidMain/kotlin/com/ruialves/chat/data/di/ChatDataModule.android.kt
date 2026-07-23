package com.ruialves.chat.data.di

import org.koin.android.ext.koin.androidContext
import com.ruialves.chat.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformChatDataModule: Module = module {
    single { DatabaseFactory(androidContext()) }
}
