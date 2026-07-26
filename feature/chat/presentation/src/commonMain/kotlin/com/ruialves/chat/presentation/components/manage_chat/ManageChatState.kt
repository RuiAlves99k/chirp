package com.ruialves.chat.presentation.components.manage_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.presentation.util.UiText

data class ManageChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val existingChatParticipants: List<ChatParticipantUi> = emptyList(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isSearching: Boolean = false,
    val canAddParticipant: Boolean = false,
    val isSubmitting: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null,
    val submitError: UiText? = null,
)
