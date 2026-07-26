package com.ruialves.chat.domain.chat

import com.ruialves.chat.domain.models.Chat
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result

interface ChatService {
    suspend fun createChat(
        otherUserIds: List<String>,
    ): Result<Chat, DataError.Remote>

    suspend fun getChats(): Result<List<Chat>, DataError.Remote>

    suspend fun getChatById(chatId: String): Result<Chat, DataError.Remote>

    suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote>
}
