package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.no_messages
import chirp.feature.chat.presentation.generated.resources.no_messages_subtitle
import chirp.feature.chat.presentation.generated.resources.retry
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.presentation.components.EmptySection
import com.ruialves.chat.presentation.models.MessageUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.buttons.ChirpButtonStyle
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.presentation.util.UiText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
fun MessageList(
    messages: List<MessageUi>,
    paginationError: String?,
    isPaginationLoading: Boolean,
    messageWithOpenMenu: MessageUi.LocalUserMessage?,
    listState: LazyListState,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onMessageRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onRetryPaginationClick: () -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteMessageClick: (MessageUi.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    if (messages.isEmpty()) {
        Box(
            modifier = modifier.padding(vertical = 32.dp), contentAlignment = Alignment.Center
        ) {
            EmptySection(
                title = stringResource(Res.string.no_messages),
                description = stringResource(Res.string.no_messages_subtitle)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(16.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = messages,
            ) { message ->
                MessageListItemUi(
                    messageUi = message,
                    messageWithOpenMenu = messageWithOpenMenu,
                    onMessageLongClick = onMessageLongClick,
                    onRetryClick = onMessageRetryClick,
                    onDeleteClick = onDeleteMessageClick,
                    onDismissMessageMenu = onDismissMessageMenu,
                    modifier = Modifier.fillMaxWidth().animateItem()
                )
            }

            when {
                isPaginationLoading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                paginationError != null -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ChirpButton(
                                text = stringResource(Res.string.retry),
                                onClick = onRetryPaginationClick,
                                style = ChirpButtonStyle.SECONDARY
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = paginationError,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun MessageListEmptyPreview() {
    ChirpTheme {
        MessageList(
            listState = rememberLazyListState(),
            messages = emptyList(),
            messageWithOpenMenu = null,
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onRetryPaginationClick = {},
            paginationError = null,
            isPaginationLoading = false,
        )
    }
}


@Composable
@Preview
private fun MessageListPreview() {
    ChirpTheme {
        MessageList(
            listState = rememberLazyListState(),
            messages = (1..20).map {
                val showLocalMessages = Random.nextBoolean()
                if (showLocalMessages) {
                    MessageUi.LocalUserMessage(
                        id = it.toString(),
                        content = "Hello world!",
                        deliveryStatus = ChatMessageDeliveryStatus.SENT,
                        formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                    )
                } else {
                    MessageUi.OtherUserMessage(
                        id = it.toString(),
                        content = "Hello other",
                        formattedSentTime = UiText.DynamicString("Saturday, Aug 21"),
                        sender = ChatParticipantUi(
                            id = "-1", username = "Rui Alves", initials = "RA"
                        )
                    )
                }
            },
            messageWithOpenMenu = null,
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onRetryPaginationClick = {},
            paginationError = null,
            isPaginationLoading = false,
        )
    }
}

@Composable
@Preview
private fun MessageListDarkPreview() {
    ChirpTheme(darkTheme = true) {
        MessageList(
            listState = rememberLazyListState(),
            messages = (1..20).map {
                val showLocalMessages = Random.nextBoolean()
                if (showLocalMessages) {
                    MessageUi.LocalUserMessage(
                        id = it.toString(),
                        content = "Hello world!",
                        deliveryStatus = ChatMessageDeliveryStatus.SENT,
                        formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                    )
                } else {
                    MessageUi.OtherUserMessage(
                        id = it.toString(),
                        content = "Hello other",
                        formattedSentTime = UiText.DynamicString("Saturday, Aug 21"),
                        sender = ChatParticipantUi(
                            id = "-1", username = "Rui Alves", initials = "RA"
                        )
                    )
                }
            },
            messageWithOpenMenu = null,
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {},
            onRetryPaginationClick = {},
            paginationError = null,
            isPaginationLoading = false,
        )
    }
}
