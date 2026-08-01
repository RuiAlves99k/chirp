package com.ruialves.chat.data.message

import com.ruialves.chat.data.dto.ChatMessageDto
import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.domain.message.ChatMessageService
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.core.data.networking.get
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.map
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
): ChatMessageService {

    override suspend fun fetchMessages(chatId: String, before: String?): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                if (before != null){
                    this["before"] = before
                }
            }
        ).map { it.map { it.toDomain() } }
    }
}
