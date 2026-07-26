package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatDto
import com.ruialves.chat.database.entities.ChatEntity
import com.ruialves.chat.database.entities.ChatInfoEntity
import com.ruialves.chat.database.entities.ChatWithParticipants
import com.ruialves.chat.database.entities.MessageWithSender
import com.ruialves.chat.domain.models.Chat
import com.ruialves.chat.domain.models.ChatInfo
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.domain.models.ChatParticipant
import kotlin.time.Instant

typealias DataMessageWithSender = MessageWithSender
typealias DomainMessageWithSender = com.ruialves.chat.domain.models.MessageWithSender
fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    participants = participants.map { it.toDomain() },
    lastActivityAt = Instant.parse(lastActivityAt),
    lastMessage = lastMessage?.toDomain()
)

fun ChatEntity.toDomain(
    participants: List<ChatParticipant>,
    lastMessage: ChatMessage? = null
): Chat = Chat(
    id = chatId,
    participants = participants,
    lastActivityAt = Instant.fromEpochMilliseconds(lastActivityAt),
    lastMessage = lastMessage
)

fun ChatWithParticipants.toDomain(): Chat {
    return Chat(
        id = chat.chatId,
        participants = participants.map { it.toDomain() },
        lastActivityAt = Instant.fromEpochMilliseconds(chat.lastActivityAt),
        lastMessage = lastMessage?.toDomain(),
    )
}
fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        chatId = id,
        lastActivityAt = lastActivityAt.toEpochMilliseconds(),
        lastMessage = lastMessage?.content
    )
}

fun DataMessageWithSender.toDomain() = DomainMessageWithSender(
    message = message.toDomain(),
    sender = sender.toDomain(),
    deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.message.deliveryStatus)

)

fun ChatInfoEntity.toDomain(): ChatInfo {
    return ChatInfo(
        chat = chat.toDomain(
            participants = participants.map { it.toDomain() },
        ),
        messages = messagesWithSenders.map { it.toDomain() }
    )
}
