package com.ruialves.chat.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.group_chat
import com.ruialves.chat.presentation.models.ChatUi
import com.ruialves.core.designsystem.components.avatar.ChirpStackedAvatars
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.designsystem.theme.titleXSmall
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatItemHeaderRow(
    chat: ChatUi,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isGroupChat = chat.otherParticipants.size > 1
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChirpStackedAvatars(
            avatars = chat.otherParticipants
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isGroupChat) {
                    stringResource(Res.string.group_chat)
                } else {
                    chat.otherParticipants.first().username
                },
                style = MaterialTheme.typography.titleXSmall,
                color = MaterialTheme.colorScheme.extended.textPrimary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            if (isGroupChat) {
                val formattedUsernames = remember(chat.otherParticipants){
                    chat.otherParticipants.joinToString {
                        it.username
                    }
                }
                Text(
                    text = formattedUsernames,
                    color = MaterialTheme.colorScheme.extended.textPlaceholder,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
