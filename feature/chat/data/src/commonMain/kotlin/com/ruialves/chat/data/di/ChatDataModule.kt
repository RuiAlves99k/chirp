package com.ruialves.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ruialves.chat.data.chat.KtorChatParticipantService
import com.ruialves.chat.data.chat.KtorChatService
import com.ruialves.chat.data.chat.OfflineFirstChatRepository
import com.ruialves.chat.data.chat.WebSocketChatConnectionClient
import com.ruialves.chat.data.lifecycle.AppLifecycleObserver
import com.ruialves.chat.data.message.OfflineFirstMessageRepository
import com.ruialves.chat.data.network.KtorWebSocketConnector
import com.ruialves.chat.database.DatabaseFactory
import com.ruialves.chat.domain.chat.ChatConnectionClient
import com.ruialves.chat.domain.chat.ChatParticipantService
import com.ruialves.chat.domain.chat.ChatRepository
import com.ruialves.chat.domain.chat.ChatService
import com.ruialves.chat.domain.message.MessageRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module
val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(::OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(::OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(::WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::KtorWebSocketConnector)
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
