package com.ruialves.chat.presentation.chat_list

sealed interface ChatListAction {
    object Logout: ChatListAction
}
