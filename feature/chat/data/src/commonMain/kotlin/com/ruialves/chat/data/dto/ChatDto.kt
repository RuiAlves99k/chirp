package com.ruialves.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
class ChatDto(
    val id: String,
    val participants: List<ChatParticipantDto>,
    val lastActivityAt: String,
    val lastMessage: ChatMessageDto?
) {
}
