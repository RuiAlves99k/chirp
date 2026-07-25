package com.ruialves.chat.domain.chat

import com.ruialves.chat.domain.models.Chat
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun fetchChats(): Result<List<Chat>, DataError.Remote>
}
