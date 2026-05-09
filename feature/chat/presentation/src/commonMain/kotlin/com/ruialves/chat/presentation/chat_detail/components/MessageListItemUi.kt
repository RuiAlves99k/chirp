package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.presentation.models.MessageUi
import com.ruialves.chat.presentation.util.getChatBubbleColorForUser
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.UiText
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageListItemUi(
    messageUi: MessageUi,
    onDismissMessageMenu: () -> Unit,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onDeleteClick: (MessageUi.LocalUserMessage) -> Unit,
    onRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        when (messageUi) {
            is MessageUi.DateSeparator -> DateSeparatorUi(
                date = messageUi.date.asString(),
                modifier = Modifier.fillMaxWidth()
            )

            is MessageUi.LocalUserMessage -> LocalUserMessage(
                message = messageUi,
                onMessageLongClick = { onMessageLongClick(messageUi) },
                onDeleteClick = { onDeleteClick(messageUi) },
                onRetryClick = { onRetryClick(messageUi) },
                onDismissMessageMenu = onDismissMessageMenu
            )

            is MessageUi.OtherUserMessage -> OtherUserMessage(
                message = messageUi,
                color = getChatBubbleColorForUser(messageUi.sender.id)
            )
        }
    }
}

@Composable
private fun DateSeparatorUi(
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = date,
            modifier = Modifier
                .padding(horizontal = 40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.extended.textPlaceholder
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview
private fun MessageListItemUiLocalMessageSentPreview() {
    ChirpTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "1",
                content = "Hello World, this is a preview",
                deliveryStatus = ChatMessageDeliveryStatus.SENT,
                isMenuOpen = true,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
            ),
            onRetryClick = {},
            onDeleteClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}


@Composable
@Preview
private fun MessageListItemUiLocalMessageRetryPreview() {
    ChirpTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "1",
                content = "Hello World, this is a preview",
                deliveryStatus = ChatMessageDeliveryStatus.FAILED,
                isMenuOpen = false,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
            ),
            onRetryClick = {},
            onDeleteClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
@Preview
private fun MessageListItemUiLocalMessageSendingPreview() {
    ChirpTheme {
        MessageListItemUi(
            messageUi = MessageUi.LocalUserMessage(
                id = "1",
                content = "Hello World, this is a preview",
                deliveryStatus = ChatMessageDeliveryStatus.SENDING,
                isMenuOpen = false,
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
            ),
            onRetryClick = {},
            onDeleteClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
@Preview
private fun MessageListItemUiOtherMessagePreview() {
    ChirpTheme {
        MessageListItemUi(
            messageUi = MessageUi.OtherUserMessage(
                id = "1",
                content = "Hello World, this is a preview",
                formattedSentTime = UiText.DynamicString("Friday 2:20pm"),
                sender = ChatParticipantUi(
                    id = "1",
                    username = "Rui Alves",
                    initials = "RA"
                )
            ),
            onRetryClick = {},
            onDeleteClick = {},
            onMessageLongClick = {},
            onDismissMessageMenu = {},
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
