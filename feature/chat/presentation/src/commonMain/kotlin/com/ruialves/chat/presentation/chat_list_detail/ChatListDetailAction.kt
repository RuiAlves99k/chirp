package com.ruialves.chat.presentation.chat_list_detail

sealed interface ChatListDetailAction {
    data class OnSelectedChat(val chatId: String?): ChatListDetailAction
    data object OnProfileSettingsClick: ChatListDetailAction
    data object OnCreateChatClick: ChatListDetailAction
    data object OnManageChatClick: ChatListDetailAction
    data object OnDismissCurrentDialog: ChatListDetailAction
}
