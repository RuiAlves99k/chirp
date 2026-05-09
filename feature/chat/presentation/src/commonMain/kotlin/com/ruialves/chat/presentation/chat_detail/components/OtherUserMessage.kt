package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ruialves.chat.presentation.models.MessageUi
import com.ruialves.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.ruialves.core.designsystem.components.chat.ChirpChatBubble
import com.ruialves.core.designsystem.components.chat.TrianglePosition

@Composable
fun OtherUserMessage(
    message: MessageUi.OtherUserMessage,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChirpAvatarPhoto(
            displayText = message.sender.initials,
            imageUrl = message.sender.imageUrl
        )
        ChirpChatBubble(
            messageContent = message.content,
            trianglePosition = TrianglePosition.LEFT,
            formattedDateTime = message.formattedSentTime.asString(),
            sender = message.sender.username,
            color = color
        )
    }
}
