package com.ruialves.chat.domain.chat

import com.ruialves.chat.domain.models.Chat
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result

interface ChatService {
    suspend fun createChat(
        otherUserIds: List<String>,
    ): Result<Chat, DataError.Remote>
}
