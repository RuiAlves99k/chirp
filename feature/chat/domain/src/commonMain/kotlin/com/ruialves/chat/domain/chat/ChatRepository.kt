package com.ruialves.chat.domain.chat

import com.ruialves.chat.domain.models.Chat
import com.ruialves.chat.domain.models.ChatInfo
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getChatInfoById(chatId: String): Flow<ChatInfo>
    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>

    suspend fun fetchChatById(chatId: String): EmptyResult<DataError.Remote>
    suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote>
    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>
}
