package com.ruialves.chat.presentation.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chirp.core.designsystem.generated.resources.app_name
import chirp.core.designsystem.generated.resources.log_out_icon
import chirp.core.designsystem.generated.resources.logo_chirp
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.logout
import chirp.feature.chat.presentation.generated.resources.profile_settings
import chirp.feature.chat.presentation.generated.resources.users_icon
import com.ruialves.chat.presentation.components.ChatHeader
import com.ruialves.core.designsystem.components.avatar.ChatParticipantUi
import com.ruialves.core.designsystem.components.avatar.ChirpAvatarPhoto
import com.ruialves.core.designsystem.components.dropdown.ChirpDropDownItem
import com.ruialves.core.designsystem.components.dropdown.ChirpDropDownMenu
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import chirp.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatListHeader(
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_chirp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(DesignSystemRes.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(Modifier.weight(1f))
            ProfileAvatarSection(
                localParticipant = localParticipant,
                isUserMenuOpen = isUserMenuOpen,
                onClick = onUserAvatarClick,
                onDismissMenu = onDismissMenu,
                onLogoutClick = onLogoutClick,
                onProfileSettingsClick = onProfileSettingsClick
            )
        }
    }
}

@Composable
private fun ProfileAvatarSection(
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        localParticipant?.let {
            ChirpAvatarPhoto(
                displayText = localParticipant.initials,
                imageUrl = localParticipant.imageUrl,
                onClick = onClick
            )
        }

        ChirpDropDownMenu(
            isOpen = isUserMenuOpen,
            onDismiss = onDismissMenu,
            items = listOf(
                ChirpDropDownItem(
                    title = stringResource(Res.string.profile_settings),
                    icon = vectorResource(Res.drawable.users_icon),
                    contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                    onClick = onProfileSettingsClick,
                ),
                ChirpDropDownItem(
                    title = stringResource(Res.string.logout),
                    icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                    contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                    onClick = onLogoutClick
                )
            )
        )
    }
}


@Composable
@Preview(showBackground = true)
fun ChatListHeaderPreview() {
    ChirpTheme {
        ChatListHeader(
            modifier = Modifier.fillMaxSize(),
            localParticipant = ChatParticipantUi(
                id = "1",
                username = "Rui",
                initials = "RU"
            ),
            isUserMenuOpen = true,
            onProfileSettingsClick = {},
            onDismissMenu = {},
            onLogoutClick = {},
            onUserAvatarClick = {}
        )
    }
}
