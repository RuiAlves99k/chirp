package com.ruialves.chat.presentation.manage_chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chirp.feature.chat.presentation.generated.resources.Res
import chirp.feature.chat.presentation.generated.resources.chat_members
import chirp.feature.chat.presentation.generated.resources.save
import com.ruialves.chat.presentation.components.manage_chat.ManageChatAction
import com.ruialves.chat.presentation.create_chat.ManageChatScreen
import com.ruialves.core.designsystem.components.dialogs.ChirpAdaptiveDialogSheetLayout
import com.ruialves.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageChatRoot(
    chatId: String?,
    onDismiss: () -> Unit,
    onMembersAdded: () -> Unit,
    viewModel: ManageChatViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(chatId) {
        viewModel.onAction(ManageChatAction.ChatParticipants.OnSelectChat(chatId))
    }

    ObserveAsEvents(viewModel.event){ event ->
        when(event){
            ManageChatEvent.OnMembersAdded -> onMembersAdded()
        }
    }

    ChirpAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ManageChatScreen(
            headerText = stringResource(Res.string.chat_members),
            primaryButtonText = stringResource(Res.string.save),
            state = state,
            onAction = { action ->
                when (action) {
                    ManageChatAction.OnDismissDialog -> onDismiss()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

