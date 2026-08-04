package com.ruialves.chat.presentation.mappers

import com.ruialves.chat.domain.models.Chat
import com.ruialves.chat.presentation.models.ChatUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.domain.auth.User

fun Chat.toUi(localParticipantId: String): ChatUi {
    val (local, other) = participants.partition { it.userId == localParticipantId }
    return ChatUi(
        id = id,
        localParticipant = local.first().toUi(),
        otherParticipants = other.map { it.toUi() },
        lastMessage = lastMessage,
        lastMessageSenderUsername = participants
            .find { it.userId == lastMessage?.senderId }
            ?.username
    )
}

fun User.toUi(): ChatParticipantUi {
    return ChatParticipantUi(
        id = id,
        username = username,
        initials = username.take(2).uppercase(),
        imageUrl = profilePictureUrl
    )
}
