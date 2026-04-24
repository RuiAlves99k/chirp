package com.ruialves.chat.presentation.chat_list

sealed interface ChatListEvent {
    object Logout: ChatListEvent
}
