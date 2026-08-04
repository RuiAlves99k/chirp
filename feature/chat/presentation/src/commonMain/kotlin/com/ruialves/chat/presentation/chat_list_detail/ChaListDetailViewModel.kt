package com.ruialves.chat.presentation.chat_list_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruialves.chat.domain.chat.ChatConnectionClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ChaListDetailViewModel(
    private val connectionClient: ChatConnectionClient
) : ViewModel() {

    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(ChatListDetailState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                connectionClient.chatMessages.launchIn(viewModelScope)
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListDetailState()
        )

    fun onAction(action: ChatListDetailAction) {
        when (action) {
            is ChatListDetailAction.OnSelectedChat -> {
                _state.update {
                    it.copy(
                        selectedChatId = action.chatId
                    )
                }
            }

            ChatListDetailAction.OnCreateChatClick -> {
                _state.update { it.copy(
                    dialogState = ChatListDetailDialogState.CreateChat
                ) }
            }

            ChatListDetailAction.OnDismissCurrentDialog -> {
                _state.update { it.copy(
                    dialogState = ChatListDetailDialogState.Hidden
                ) }
            }
            ChatListDetailAction.OnManageChatClick -> {
                state.value.selectedChatId?.let { id ->
                    _state.update { it.copy(
                        dialogState = ChatListDetailDialogState.ManageChat(id)
                    ) }
                }
            }
            ChatListDetailAction.OnProfileSettingsClick -> {
                _state.update { it.copy(
                    dialogState = ChatListDetailDialogState.Profile
                ) }
            }
        }
    }
}
