package com.ruialves.chat.presentation.chat_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import chirp.core.designsystem.generated.resources.cloud_off_icon
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.offline
import chirp.feature.chat.presentation.generated.resources.send
import chirp.feature.chat.presentation.generated.resources.send_a_message
import chirp.core.designsystem.generated.resources.Res as DesingSystemRes
import com.ruialves.chat.domain.models.ConnectionState
import com.ruialves.chat.presentation.util.toUiText
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.textfields.ChirpMultiLineTextField
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageBox(
    messageTextFieldState: TextFieldState,
    isTextInputEnabled: Boolean,
    connectionState: ConnectionState,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    ChirpMultiLineTextField(
        state = messageTextFieldState,
        modifier = modifier,
        enabled = isTextInputEnabled,
        placeholder = stringResource(Res.string.send_a_message),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Send
        ),
        onKeyboardAction = onSendClick,
        bottomContent = {
            Spacer(modifier = Modifier.weight(1f))
            if (!isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = vectorResource(DesingSystemRes.drawable.cloud_off_icon),
                        contentDescription = stringResource(Res.string.offline),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.extended.textDisabled
                    )
                    Text(
                        text = connectionState.toUiText().asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.extended.textDisabled
                    )
                }
            }
            ChirpButton(
                text = stringResource(Res.string.send),
                enabled = isConnected && isTextInputEnabled,
                onClick = onSendClick
            )
        }
    )
}


@Composable
@Preview
fun MessageBoxDisconnectedPreview() {
    ChirpTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isTextInputEnabled = false,
                connectionState = ConnectionState.DISCONNECTED,
                onSendClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun MessageBoxConnectedPreview() {
    ChirpTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            MessageBox(
                messageTextFieldState = rememberTextFieldState(),
                isTextInputEnabled = false,
                connectionState = ConnectionState.CONNECTED,
                onSendClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
