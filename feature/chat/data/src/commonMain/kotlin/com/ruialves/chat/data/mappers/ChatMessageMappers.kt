package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatMessageDto
import com.ruialves.chat.database.entities.ChatMessageEntity
import com.ruialves.chat.database.view.LastMessageView
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    chatId = chatId,
    content = content,
    createdAt = Instant.parse(createdAt),
    senderId = senderId,
    deliveryStatus = ChatMessageDeliveryStatus.SENT
)

fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    id = chatId,
    chatId = chatId,
    content = content,
    createdAt = Instant.fromEpochMilliseconds(timestamp),
    senderId = senderId,
    deliveryStatus = ChatMessageDeliveryStatus.SENT
)

fun LastMessageView.toDomain(): ChatMessage {
    return ChatMessage(
        id = messageId,
        chatId = chatId,
        content = content,
        createdAt = Instant.fromEpochMilliseconds(timestamp),
        senderId = senderId,
        deliveryStatus = ChatMessageDeliveryStatus.valueOf(this.deliveryStatus)
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        messageId = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        deliveryStatus = deliveryStatus.name
    )
}

fun ChatMessage.toLastMessageView(): LastMessageView {
    return LastMessageView(
        messageId = id,
        chatId = chatId,
        senderId = senderId,
        content = content,
        timestamp = createdAt.toEpochMilliseconds(),
        deliveryStatus = deliveryStatus.name
    )
}
