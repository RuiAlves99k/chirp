package com.ruialves.chat.presentation.chat_list

import com.ruialves.chat.presentation.models.ChatUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.presentation.util.UiText

data class ChatListState(
    val chats: List<ChatUi> = emptyList(),
    val error: UiText? = null,
    val localParticipant: ChatParticipantUi? =null,
    val isUserMenuOpen: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val selectedChatId: String? = null,
    val isLoading: Boolean = false
)
