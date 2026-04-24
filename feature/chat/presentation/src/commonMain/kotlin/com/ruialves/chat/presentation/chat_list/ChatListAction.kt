package com.ruialves.chat.presentation.chat_list

sealed interface ChatListAction {
    data object Logout: ChatListAction
}
