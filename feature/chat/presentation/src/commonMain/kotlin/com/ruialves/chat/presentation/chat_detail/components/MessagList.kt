package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.no_messages
import chirp.feature.chat.presentation.generated.resources.no_messages_subtitle
import com.ruialves.chat.domain.models.ChatMessageDeliveryStatus
import com.ruialves.chat.presentation.components.EmptySection
import com.ruialves.chat.presentation.models.MessageUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.presentation.util.UiText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
fun MessageList(
    messages: List<MessageUi>,
    listState: LazyListState,
    onMessageLongClick: (MessageUi.LocalUserMessage) -> Unit,
    onMessageRetryClick: (MessageUi.LocalUserMessage) -> Unit,
    onDismissMessageMenu: () -> Unit,
    onDeleteMessageClick: (MessageUi.LocalUserMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    if (messages.isEmpty()){
        Box(
            modifier = Modifier
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
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
                key = { it.id }
            ) { message ->
                MessageListItemUi(
                    messageUi = message,
                    onMessageLongClick = onMessageLongClick,
                    onRetryClick = onMessageRetryClick,
                    onDeleteClick = onDeleteMessageClick,
                    onDismissMessageMenu = onDismissMessageMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                )
            }
        }
    }
}

@Composable
@Preview
private fun MessageListEmptyPreview(){
    ChirpTheme {
        MessageList(
            listState = rememberLazyListState(),
            messages = emptyList(),
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {}
        )
    }
}


@Composable
@Preview
private fun MessageListPreview(){
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
                        isMenuOpen = false,
                        formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                    )
                } else {
                    MessageUi.OtherUserMessage(
                        id = it.toString(),
                        content = "Hello other",
                        formattedSentTime = UiText.DynamicString("Saturday, Aug 21"),
                        sender = ChatParticipantUi(
                            id = "-1",
                            username = "Rui Alves",
                            initials = "RA"
                        )
                    )
                }
            },
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {}
        )
    }
}

@Composable
@Preview
private fun MessageListDarkPreview(){
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
                        isMenuOpen = false,
                        formattedSentTime = UiText.DynamicString("Friday, Aug 20"),
                    )
                } else {
                    MessageUi.OtherUserMessage(
                        id = it.toString(),
                        content = "Hello other",
                        formattedSentTime = UiText.DynamicString("Saturday, Aug 21"),
                        sender = ChatParticipantUi(
                            id = "-1",
                            username = "Rui Alves",
                            initials = "RA"
                        )
                    )
                }
            },
            onDismissMessageMenu = {},
            onDeleteMessageClick = {},
            onMessageLongClick = {},
            onMessageRetryClick = {}
        )
    }
}
