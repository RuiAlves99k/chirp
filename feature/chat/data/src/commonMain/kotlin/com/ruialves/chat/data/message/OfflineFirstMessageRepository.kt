package com.ruialves.chat.data.message

import com.ruialves.chat.data.dto.websocket.OutgoingWebSocketDto
import com.ruialves.chat.data.dto.websocket.WebSocketMessageDto
import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.data.mappers.toEntity
import com.ruialves.chat.data.mappers.toNewMessage
import com.ruialves.chat.data.mappers.toWebSocketDto
import com.ruialves.chat.data.network.KtorWebSocketConnector
import com.ruialves.chat.database.ChirpChatDatabase
import com.ruialves.chat.domain.message.ChatMessageService
import com.ruialves.chat.domain.message.MessageRepository
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.domain.models.MessageWithSender
import com.ruialves.chat.domain.models.OutgoingNewMessage
import com.ruialves.core.data.database.safeDatabaseUpdate
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.onFailure
import com.ruialves.core.domain.util.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: ChirpChatDatabase,
    private val chatMessageService: ChatMessageService,
    private val sessionStorage: SessionStorage,
    private val webSocketConnector: KtorWebSocketConnector,
    private val json: Json,
    private val applicationScope: CoroutineScope
): MessageRepository {
    override suspend fun updateMessageDeliveryStatus(
        messageId: String,
        status: ChatMessageDeliveryStatus
    ): EmptyResult<DataError.Local> {
        return safeDatabaseUpdate {
            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                status = status.name,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError> {
        return chatMessageService
            .fetchMessages(chatId, before)
            .onSuccess { messages ->
                return safeDatabaseUpdate {
                    database.chatMessageDao.upsertMessagesAndSyncIfNecessary(
                        chatId = chatId,
                        serverMessages = messages.map { it.toEntity() },
                        pageSize = ChatMessageConstants.PAGE_SIZE,
                        shouldSync = before == null // Only sync for most recent page
                    )
                    messages
                }
            }
    }

    override fun getMessagesForChat(chatId: String): Flow<List<MessageWithSender>> {
        return database.chatMessageDao
            .getMessagesByChatId(chatId)
            .map { messages ->
                messages.map { it.toDomain() }
            }
    }

    override suspend fun sendMessage(message: OutgoingNewMessage): EmptyResult<DataError> {
       return safeDatabaseUpdate {
           val dto = message.toWebSocketDto()

           val localUser = sessionStorage.observeAuthInfo().firstOrNull()?.user
               ?: return Result.Failure(DataError.Local.NOT_FOUND)

           val entity = dto.toEntity(
               senderId = localUser.id,
               deliveryStatus = ChatMessageDeliveryStatus.SENDING
           )

           database.chatMessageDao.upsertMessage(entity)

           return webSocketConnector
               .sendMessage(dto.toJsonPayload())
               .onFailure { error ->
                   applicationScope.launch {
                       database.chatMessageDao.upsertMessage(
                           dto.toEntity(
                               senderId = localUser.id,
                               deliveryStatus = ChatMessageDeliveryStatus.FAILED
                           )
                       )
                   }.join()
               }

       }
    }

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return chatMessageService
            .deleteMessage(messageId)
            .onSuccess {
                applicationScope.launch {
                    database.chatMessageDao.deleteMessageById(messageId)
                }.join()
            }
    }

    override suspend fun retryMessage(messageId: String): EmptyResult<DataError> {
        return safeDatabaseUpdate {
            val message = database.chatMessageDao.getMessageById(messageId)
                ?: return Result.Failure(DataError.Local.NOT_FOUND)

            database.chatMessageDao.updateDeliveryStatus(
                messageId = messageId,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                status = ChatMessageDeliveryStatus.SENDING.name
            )

            val outgoingNewMessage = OutgoingWebSocketDto.NewMessage(
                chatId = message.chatId,
                messageId = messageId,
                content = message.content
            )

            return webSocketConnector
                .sendMessage(outgoingNewMessage.toJsonPayload())
                .onFailure {
                    applicationScope.launch {
                        database.chatMessageDao.updateDeliveryStatus(
                            messageId = messageId,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
                            status = ChatMessageDeliveryStatus.FAILED.name
                        )
                    }.join()
                }
        }
    }

    private fun OutgoingWebSocketDto.NewMessage.toJsonPayload(): String {
        val webSocketMessage = WebSocketMessageDto(
            type = type.name,
            payload = json.encodeToString(this)
        )

        return json.encodeToString(webSocketMessage)
    }
}
