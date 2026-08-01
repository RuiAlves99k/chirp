package com.ruialves.chat.presentation.mappers

import com.ruialves.chat.domain.models.MessageWithSender
import com.ruialves.chat.presentation.models.MessageUi
import com.ruialves.chat.presentation.util.DateUtils

fun MessageWithSender.toUi(
    localUserId: String,
): MessageUi {
    val isFromLocalUser = sender.userId == localUserId
    return if (isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            isMenuOpen = false,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt)
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = sender.toUi()
        )
    }
}
