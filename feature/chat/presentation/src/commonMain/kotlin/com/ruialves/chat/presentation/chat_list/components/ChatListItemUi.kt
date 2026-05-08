package com.ruialves.chat.presentation.chat_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.presentation.components.ChatItemHeaderRow
import com.ruialves.chat.presentation.models.ChatUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatListItemUi(
    chat: ChatUi,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClickChat: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable {
                onClickChat()
            }
            .height(IntrinsicSize.Min)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.extended.surfaceLower
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChatItemHeaderRow(
                chat = chat,
                modifier = Modifier.fillMaxWidth()
            )

            if (chat.lastMessage != null) {
                val previewMessage = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.extended.textSecondary,
                        )
                    ) {
                        append(chat.lastMessageSenderUsername + ": ")
                    }
                    append(chat.lastMessage.content)
                }
                Text(
                    text = previewMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.extended.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier = Modifier
                .alpha(if (isSelected) 1f else 0f)
                .background(MaterialTheme.colorScheme.primary)
                .width(4.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
@Preview
fun ChatListItemUiPreview(){
    ChirpTheme {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "Rui",
                    initials = "RA",
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Lily",
                        initials = "Li",
                    ),
                    ChatParticipantUi(
                        id = "3",
                        username = "John",
                        initials = "JO",
                    ),
                    ChatParticipantUi(
                        id = "4",
                        username = "Kai",
                        initials = "KA",
                    )
                ),
                lastMessage = ChatMessage(
                    id = "1",
                    chatId = "1",
                    content = "Hello everyone",
                    createdAt = Clock.System.now(),
                    senderId = "2"
                ),
                lastMessageSenderUsername = "Lily"
            ),
            isSelected = true,
            onClickChat = {}
        )
    }
}

@Composable
@Preview
fun ChatListItemUiDarkPreview(){
    ChirpTheme(darkTheme = true) {
        ChatListItemUi(
            chat = ChatUi(
                id = "1",
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "Rui",
                    initials = "RA",
                ),
                otherParticipants = listOf(
                    ChatParticipantUi(
                        id = "2",
                        username = "Lily",
                        initials = "Li",
                    ),
                ),
                lastMessage = ChatMessage(
                    id = "1",
                    chatId = "1",
                    content = "Hello, how are you?",
                    createdAt = Clock.System.now(),
                    senderId = "2"
                ),
                lastMessageSenderUsername = "Lily"
            ),
            isSelected = true,
            onClickChat = {}
        )
    }
}
