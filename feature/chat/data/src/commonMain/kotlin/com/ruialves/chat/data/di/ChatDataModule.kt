package com.ruialves.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ruialves.chat.data.chat.KtorChatParticipantService
import com.ruialves.chat.data.chat.KtorChatService
import com.ruialves.chat.database.DatabaseFactory
import com.ruialves.chat.domain.chat.ChatParticipantService
import com.ruialves.chat.domain.chat.ChatService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
