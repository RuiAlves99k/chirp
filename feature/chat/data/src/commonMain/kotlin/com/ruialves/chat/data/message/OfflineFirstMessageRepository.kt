package com.ruialves.chat.data.message

import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.data.mappers.toEntity
import com.ruialves.chat.database.ChirpChatDatabase
import com.ruialves.chat.domain.message.ChatMessageService
import com.ruialves.chat.domain.message.MessageRepository
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.domain.models.MessageWithSender
import com.ruialves.core.data.database.safeDatabaseUpdate
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: ChirpChatDatabase,
    private val chatMessageService: ChatMessageService,
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
}
