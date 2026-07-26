package com.ruialves.chat.data.chat

import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.data.mappers.toEntity
import com.ruialves.chat.data.mappers.toLastMessageView
import com.ruialves.chat.database.ChirpChatDatabase
import com.ruialves.chat.database.entities.ChatInfoEntity
import com.ruialves.chat.database.entities.ChatParticipantEntity
import com.ruialves.chat.database.entities.ChatWithParticipants
import com.ruialves.chat.domain.chat.ChatRepository
import com.ruialves.chat.domain.chat.ChatService
import com.ruialves.chat.domain.models.Chat
import com.ruialves.chat.domain.models.ChatInfo
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.asEmptyResult
import com.ruialves.core.domain.util.onSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope

class OfflineFirstChatRepository(
    private val chatService: ChatService, private val db: ChirpChatDatabase
) : ChatRepository {
    override fun getChats(): Flow<List<Chat>> {
        return db.chatDao.getChatsWithParticipants().map { allChatsWithParticipantsList ->
            supervisorScope {
                allChatsWithParticipantsList.map { chatWithParticipants ->
                    async {
                        ChatWithParticipants(
                            chat = chatWithParticipants.chat,
                            participants = chatWithParticipants.participants.onlyActive(chatWithParticipants.chat.chatId),
                            lastMessage = chatWithParticipants.lastMessage
                        )
                    }
                }
                    .awaitAll()
                    .map { it.toDomain() }
            }
        }
    }

    override fun getChatInfoById(chatId: String): Flow<ChatInfo> {
        return db.chatDao.getChatInfoById(chatId).filterNotNull().map { chatInfo ->
                ChatInfoEntity(
                    chat = chatInfo.chat,
                    participants = chatInfo.participants.onlyActive(chatInfo.chat.chatId),
                    messagesWithSenders = chatInfo.messagesWithSenders
                )
            }
            .map { it.toDomain() }
    }

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        return chatService.getChats()
            .onSuccess { chats ->
                val chatsWithParticipants = chats.map { chat ->
                    ChatWithParticipants(
                        chat = chat.toEntity(),
                        participants = chat.participants.map { it.toEntity() },
                        lastMessage = chat.lastMessage?.toLastMessageView(),
                    )
                }

                db.chatDao.upsertChatsWithParticipantsAndCrossRefs(
                    chats = chatsWithParticipants,
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantCrossRefDao,
                    messageDao = db.chatMessageDao
                )
            }
    }

    override suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote> {
        return chatService.getChatById(chatId)
            .onSuccess { chat ->
                db.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantCrossRefDao
                )
            }.asEmptyResult()
    }

    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return chatService
            .createChat(otherUserIds)
            .onSuccess { chat ->
                db.chatDao.upsertChatWithParticipantsAndCrossRefs(
                    chat = chat.toEntity(),
                    participants = chat.participants.map { it.toEntity() },
                    participantDao = db.chatParticipantDao,
                    crossRefDao = db.chatParticipantCrossRefDao
                )
            }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return chatService.leaveChat(chatId).onSuccess {
            db.chatDao.deleteChatById(chatId)
        }
    }

    private suspend fun List<ChatParticipantEntity>.onlyActive(chatId: String): List<ChatParticipantEntity> {
        val activeParticipantIds = db
            .chatDao
            .getActiveParticipantsByChatId(chatId)
            .first()
            .map { it.userId }

        return this.filter { it.userId in activeParticipantIds }
    }
}
