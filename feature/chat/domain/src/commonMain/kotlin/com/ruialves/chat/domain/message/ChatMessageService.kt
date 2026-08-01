package com.ruialves.chat.domain.message

import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result

interface ChatMessageService {
    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError.Remote>
}
