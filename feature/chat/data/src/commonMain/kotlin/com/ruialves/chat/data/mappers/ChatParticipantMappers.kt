package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatParticipantDto
import com.ruialves.chat.database.entities.ChatParticipantEntity
import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.core.domain.auth.User

fun ChatParticipantDto.toDomain(): ChatParticipant = ChatParticipant(
    userId = userId,
    username = username,
    profilePictureUrl = profilePictureUrl,
)

fun ChatParticipantEntity.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipant.toEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}
