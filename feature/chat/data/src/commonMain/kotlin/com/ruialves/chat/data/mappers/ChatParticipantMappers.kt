package com.ruialves.chat.data.mappers

import com.ruialves.chat.data.dto.ChatParticipantDto
import com.ruialves.chat.domain.models.ChatParticipant

fun ChatParticipantDto.toDomain(): ChatParticipant = ChatParticipant(
    userId = userId,
    username = username,
    profilePictureUrl = profilePictureUrl,
)
