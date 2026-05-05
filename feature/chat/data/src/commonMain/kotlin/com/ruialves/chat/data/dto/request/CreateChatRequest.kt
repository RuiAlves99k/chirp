package com.ruialves.chat.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
class CreateChatRequest(
    val otherUserIds: List<String>
)
