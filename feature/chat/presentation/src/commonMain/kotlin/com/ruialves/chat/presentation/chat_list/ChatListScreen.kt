package com.ruialves.chat.presentation.chat_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import kotlinx.serialization.Serializable

@Composable
fun ChatListScreenRoot(
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chat List Screen",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        ChirpButton(
            text = "Logout",
            onClick = {
                onLogout()
            }
        )
    }
}

@Serializable
data object ChatListRoute
