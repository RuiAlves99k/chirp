package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chirp.core.designsystem.generated.resources.arrow_left_icon
import chirp.core.designsystem.generated.resources.dots_icon
import chirp.core.designsystem.generated.resources.log_out_icon
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.chat_members
import chirp.feature.chat.presentation.generated.resources.go_back
import chirp.feature.chat.presentation.generated.resources.leave_chat
import chirp.feature.chat.presentation.generated.resources.open_chat_options_menu
import chirp.feature.chat.presentation.generated.resources.users_icon
import com.ruialves.chat.domain.models.ChatMessage
import com.ruialves.chat.presentation.components.ChatHeader
import com.ruialves.chat.presentation.components.ChatItemHeaderRow
import chirp.core.designsystem.generated.resources.Res as DesignSystemRes
import com.ruialves.chat.presentation.models.ChatUi
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.components.buttons.ChirpIconButton
import com.ruialves.core.designsystem.components.dropdown.ChirpDropDownItem
import com.ruialves.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi,
    isDetailPresent: Boolean,
    isChatOptionsDropDownOpen: Boolean,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: () -> Unit,
    onManageChatClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!isDetailPresent) {
            ChirpIconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        ChatItemHeaderRow(
            chat = chatUi,
            modifier = Modifier.weight(1f),
            onClick = onManageChatClick
        )

        Box {
            ChirpIconButton(
                onClick = onChatOptionsClick,
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            ChirpDropDownMenu(
                isOpen = isChatOptionsDropDownOpen,
                onDismiss = onDismissChatOptions,
                items = listOf(
                    ChirpDropDownItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageChatClick
                    ),
                    ChirpDropDownItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    )
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ChatDetailHeaderPreview() {
    ChirpTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ChatHeader {
                ChatDetailHeader(
                    chatUi = ChatUi(
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
                    isDetailPresent = false,
                    onChatOptionsClick = {},
                    onManageChatClick = {},
                    onLeaveChatClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                    isChatOptionsDropDownOpen = true
                )
            }
        }
    }
}
