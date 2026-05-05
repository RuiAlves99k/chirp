package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatMessageDto
import com.ruialves.chat.domain.models.ChatMessage
import kotlin.time.Instant

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    chatId = chatId,
    content = content,
    createdAt = Instant.parse(createdAt),
    senderId = senderId
)
