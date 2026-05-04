package com.ruialves.chat.presentation.create_chat

import androidx.compose.foundation.text.input.TextFieldState
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.presentation.util.UiText

data class CreateChatState(
    val queryTextState: TextFieldState = TextFieldState(),
    val selectedChatParticipants: List<ChatParticipantUi> = emptyList(),
    val isSearching: Boolean = false,
    val canAddParticipant: Boolean = false,
    val isCreatingChat: Boolean = false,
    val currentSearchResult: ChatParticipantUi? = null,
    val searchError: UiText? = null
)
