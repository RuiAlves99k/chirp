package com.ruialves.chat.data.chat

import com.ruialves.chat.data.dto.ChatDto
import com.ruialves.chat.data.dto.request.CreateChatRequest
import com.ruialves.chat.data.dto.request.ParticipantsRequest
import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.domain.chat.ChatService
import com.ruialves.chat.domain.models.Chat
import com.ruialves.core.data.networking.delete
import com.ruialves.core.data.networking.get
import com.ruialves.core.data.networking.post
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.Result
import com.ruialves.core.domain.util.asEmptyResult
import com.ruialves.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get

class KtorChatService(
    private val httpClient: HttpClient
): ChatService {
    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return httpClient.post<CreateChatRequest, ChatDto>(
            route = "/chat",
            body = CreateChatRequest(
                otherUserIds = otherUserIds
            )
        ).map { it.toDomain() }
    }

    override suspend fun getChats(): Result<List<Chat>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(
            route = "/chat",
        ).map { chatDtos ->
            chatDtos.map { it.toDomain()}
        }
    }

    override suspend fun getChatById(chatId: String): Result<Chat, DataError.Remote> {
        return httpClient.get<ChatDto>(
            route = "/chat/$chatId"
        ).map { chatDto ->
            chatDto.toDomain()
        }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "/chat/$chatId/leave"
        ).asEmptyResult()
    }

    override suspend fun addParticipantsToChat(chatId: String, userIds: List<String>): Result<Chat, DataError.Remote> {
        return httpClient.post<ParticipantsRequest, ChatDto>(
            route = "/chat/$chatId/add",
            body = ParticipantsRequest(
                userIds = userIds
            )
        ).map { it.toDomain() }
    }
}
