package com.ruialves.chat.presentation.create_chat

import com.ruialves.chat.domain.models.Chat

sealed interface CreateChatEvent {
    data class OnChatCreated(val chat: Chat) : CreateChatEvent
}
