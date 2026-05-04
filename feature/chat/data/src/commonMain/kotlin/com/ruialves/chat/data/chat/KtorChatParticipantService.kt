package com.ruialves.chat.data.chat

import com.ruialves.chat.data.dto.ChatParticipantDto
import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.domain.chat.ChatParticipantService
import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.core.data.networking.get
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.map
import io.ktor.client.HttpClient

class KtorChatParticipantService(
    private val httpClient: HttpClient
): ChatParticipantService {
    override suspend fun searchParticipant(query: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = "/participants",
            queryParams = mapOf(
                "query" to query
            )
        ).map { it.toDomain() }
    }
}
