package com.ruialves.chat.presentation.mappers

import com.ruialves.chat.domain.models.ChatParticipant
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi

fun ChatParticipant.toUi() = ChatParticipantUi(
    id = userId,
    username = username,
    imageUrl = profilePictureUrl,
    initials = initials
)
