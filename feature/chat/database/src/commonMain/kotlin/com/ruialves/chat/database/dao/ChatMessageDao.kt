package com.ruialves.chat.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ruialves.chat.database.entities.ChatMessageEntity
import com.ruialves.chat.database.entities.MessageWithSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ChatMessageDao {

    @Upsert
    suspend fun upsertMessage(messages: ChatMessageEntity)

    @Upsert
    suspend fun upsertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chatmessageentity WHERE messageId = :messageId")
    suspend fun deleteMessageById(messageId: String)

    @Query("DELETE FROM chatmessageentity WHERE messageId IN (:messageIds)")
    suspend fun deleteMessageById(messageIds: List<String>)

    @Query("SELECT * FROM chatmessageentity WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): Flow<List<MessageWithSender>>

    @Query("""
        SELECT * FROM chatmessageentity
        WHERE chatId = :chatId
        ORDER BY timestamp DESC
        LIMIT :limit
        """)
    fun getMessagesByChatIdLimited(chatId: String, limit: Int): Flow<List<ChatMessageEntity>>

    @Query("SELECt * FROM chatmessageentity WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): ChatMessageEntity?

    @Query("""
        UPDATE chatmessageentity
        SET deliveryStatusTimestamp = :timestamp, deliveryStatus = :status
        WHERE messageId = :messageId
    """)
    suspend fun updateDeliveryStatus(messageId: String, status: String, timestamp: Long)

    @Transaction
    suspend fun upsertMessagesAndSyncIfNecessary(
        chatId: String,
        serverMessages: List<ChatMessageEntity>,
        pageSize: Int,
        shouldSync: Boolean = false
    ) {
        val localMessages = getMessagesByChatIdLimited(
            chatId = chatId,
            limit = pageSize
        ).first()

        upsertMessages(serverMessages)

        if (!shouldSync) {
            return
        }

        val serverIds = serverMessages.map { it.messageId }.toSet()

        val messagesToDelete = localMessages.filter { localMessages ->
            val missingOnServer = localMessages.messageId !in serverIds
            val isSent = localMessages.deliveryStatus == "SENT"

            missingOnServer && isSent
        }

        val messageIds = messagesToDelete.map { it.messageId }
        deleteMessageById(messageIds)
    }
}
