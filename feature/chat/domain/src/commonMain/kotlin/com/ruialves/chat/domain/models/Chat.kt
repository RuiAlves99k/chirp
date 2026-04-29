package com.ruialves.chat.domain.models

import com.ruialves.core.domain.auth.User
import kotlin.time.Instant

data class Chat(
    val id: String,
    val participants: List<ChatParticipant>,
    val lastActivityAt: Instant,
    val lastMessage: String?,
)
