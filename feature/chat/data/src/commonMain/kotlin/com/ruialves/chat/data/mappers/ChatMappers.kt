package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatDto
import com.ruialves.chat.domain.models.Chat
import kotlin.time.Instant

fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    participants = participants.map { it.toDomain() },
    lastActivityAt = Instant.parse(lastActivityAt),
    lastMessage = lastMessage?.toDomain()
)
