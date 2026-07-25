package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatDto
import com.ruialves.chat.database.entities.ChatEntity
import com.ruialves.chat.database.entities.ChatWithParticipants
import com.ruialves.chat.domain.models.Chat
import kotlin.time.Instant

fun ChatDto.toDomain(): Chat = Chat(
    id = id,
    participants = participants.map { it.toDomain() },
    lastActivityAt = Instant.parse(lastActivityAt),
    lastMessage = lastMessage?.toDomain()
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
