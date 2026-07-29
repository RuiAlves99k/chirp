package com.ruialves.chat.data.message

import com.ruialves.chat.database.ChirpChatDatabase
import com.ruialves.chat.domain.message.MessageRepository
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.core.data.database.safeDatabaseUpdate
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import kotlin.time.Clock

class OfflineFirstMessageRepository(
    private val database: ChirpChatDatabase
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
}
