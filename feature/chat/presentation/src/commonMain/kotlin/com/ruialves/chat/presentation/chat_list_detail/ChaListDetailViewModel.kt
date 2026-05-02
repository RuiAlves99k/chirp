package com.ruialves.chat.presentation.chat_list_detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChaListDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(ChatListDetailState())
    val state = _state.asStateFlow()

    fun onAction(action: ChatListDetailAction) {
        when (action) {
            is ChatListDetailAction.OnChatClick -> {
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
