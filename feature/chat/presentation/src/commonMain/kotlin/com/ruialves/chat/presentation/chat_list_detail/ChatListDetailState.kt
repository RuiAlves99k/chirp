package com.ruialves.chat.presentation.chat_list_detail

data class ChatListDetailState(
    val selectedChatId: String? = null,
    val dialogState: ChatListDetailDialogState = ChatListDetailDialogState.Hidden
)

sealed interface ChatListDetailDialogState {
    data object Hidden: ChatListDetailDialogState
    data object CreateChat: ChatListDetailDialogState
    data object Profile: ChatListDetailDialogState
    data class ManageChat(val chatId: String): ChatListDetailDialogState
}
