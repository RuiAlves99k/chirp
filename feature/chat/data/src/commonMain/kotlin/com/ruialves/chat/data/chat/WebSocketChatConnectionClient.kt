package com.ruialves.chat.data.chat

import com.ruialves.chat.data.dto.websocket.IncomingWebSocketDto
import com.ruialves.chat.data.dto.websocket.IncomingWebSocketType
import com.ruialves.chat.data.dto.websocket.WebSocketMessageDto
import com.ruialves.chat.data.mappers.toDomain
import com.ruialves.chat.data.mappers.toEntity
import com.ruialves.chat.data.mappers.toNewMessage
import com.ruialves.chat.data.network.KtorWebSocketConnector
import com.ruialves.chat.database.ChirpChatDatabase
import com.ruialves.chat.domain.chat.ChatConnectionClient
import com.ruialves.chat.domain.chat.ChatRepository
import com.ruialves.chat.domain.message.MessageRepository
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.domain.models.ConnectionState
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.logging.ChirpLogger
import com.ruialves.core.domain.util.ConnectionError
import com.ruialves.core.domain.util.EmptyResult
import com.ruialves.core.domain.util.onFailure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.serialization.json.Json

class WebSocketChatConnectionClient(
    private val webSocketConnector: KtorWebSocketConnector,
    private val chatRepository: ChatRepository,
    private val database: ChirpChatDatabase,
    private val sessionStorage: SessionStorage,
    private val json: Json,
    private val messageRepository: MessageRepository,
    private val applicationScope: CoroutineScope,
) : ChatConnectionClient {

    override val chatMessages: Flow<ChatMessage> = webSocketConnector
        .messages
        .mapNotNull { parseIncomingMessage(it) }
        .onEach { handleIncomingMessage(it) }
        .filterIsInstance<IncomingWebSocketDto.NewMessageDto>()
        .mapNotNull {
            database.chatMessageDao.getMessageById(it.id)?.toDomain()
        }
        .shareIn(
            applicationScope,
            SharingStarted.WhileSubscribed(5000L)
        )

    override val connectionState: StateFlow<ConnectionState> = webSocketConnector.connectionState

    override suspend fun sendChatMessage(message: ChatMessage): EmptyResult<ConnectionError> {
        val outgoingDto = message.toNewMessage()
        val webSocketMessage = WebSocketMessageDto(
            type = outgoingDto.type.name,
            payload = json.encodeToString(outgoingDto)
        )

        val rawJsonPayload = json.encodeToString(webSocketMessage)

        return webSocketConnector
            .sendMessage(rawJsonPayload)
            .onFailure { error ->
                messageRepository.updateMessageDeliveryStatus(
                    messageId = message.id,
                    status = ChatMessageDeliveryStatus.FAILED
                )
            }
    }

    private fun parseIncomingMessage(message: WebSocketMessageDto): IncomingWebSocketDto? {
        return try {
            when (IncomingWebSocketType.valueOf(message.type)) {
                IncomingWebSocketType.NEW_MESSAGE -> {
                    json.decodeFromString<IncomingWebSocketDto.NewMessageDto>(message.payload)
                }

                IncomingWebSocketType.MESSAGE_DELETED -> {
                    json.decodeFromString<IncomingWebSocketDto.MessageDeletedDto>(message.payload)

                }

                IncomingWebSocketType.PROFILE_PICTURE_UPDATED -> {
                    json.decodeFromString<IncomingWebSocketDto.ProfilePictureUpdated>(message.payload)

                }

                IncomingWebSocketType.CHAT_PARTICIPANTS_CHANGED -> {
                    json.decodeFromString<IncomingWebSocketDto.ChatParticipantsChangedDto>(message.payload)
                }
            }
        } catch (_: IllegalArgumentException) {
            ChirpLogger.e { "Incoming message type not supported ${message.type}" }
            null
        }
    }

    private suspend fun handleIncomingMessage(message: IncomingWebSocketDto) {
        when (message){
            is IncomingWebSocketDto.ChatParticipantsChangedDto -> refreshChat(message)
            is IncomingWebSocketDto.MessageDeletedDto -> deleteMessage(message)
            is IncomingWebSocketDto.NewMessageDto -> handleNewMessage(message)
            is IncomingWebSocketDto.ProfilePictureUpdated -> updateProfilePicture(message)
        }
    }

    private suspend fun updateProfilePicture(message: IncomingWebSocketDto.ProfilePictureUpdated) {
        database.chatParticipantDao.updateProfilePictureUrl(
            userId = message.userId,
            newUrl = message.newUrl
        )

        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
        if (authInfo != null && message.userId == authInfo.user.id) {
            sessionStorage.set(
                info = authInfo.copy(
                    user = authInfo.user.copy(
                        profilePictureUrl = message.newUrl
                    )
                )
            )
        }
    }

    private suspend fun handleNewMessage(message: IncomingWebSocketDto.NewMessageDto) {
        val chatExists = database.chatDao.getChatById(message.chatId) != null
        if (!chatExists) {
            chatRepository.fetchChatById(message.chatId)
        }

        val entity = message.toEntity()
        database.chatMessageDao.upsertMessage(entity)


    }

    private suspend fun deleteMessage(message: IncomingWebSocketDto.MessageDeletedDto) {
        database.chatMessageDao.deleteMessageById(message.messageId)
    }

    private suspend fun refreshChat(message: IncomingWebSocketDto.ChatParticipantsChangedDto) {
        chatRepository.fetchChatById(message.chatId)
    }

}
