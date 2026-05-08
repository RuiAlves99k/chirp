package com.ruialves.chat.presentation.models

import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.presentation.util.UiText

sealed interface MessageUi {
    data class LocalUserMessage(
        val id: String,
        val content: String,
        val deliveryStatus: ChatMessageDeliveryStatus,
        val formattedSentTime: UiText,
        val isMenuOpen: Boolean,
    ) : MessageUi

    data class OtherUserMessage(
        val id: String,
        val content: String,
        val formattedSentTime: UiText,
        val sender: ChatParticipantUi,
    ) : MessageUi

    data class DateSeparator(
        val id: String,
        val date: UiText,
    ) : MessageUi
}
