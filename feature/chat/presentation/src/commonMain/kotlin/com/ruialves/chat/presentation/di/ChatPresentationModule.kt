package com.ruialves.chat.presentation.di

import com.ruialves.chat.presentation.chat_detail.ChatDetailViewModel
import com.ruialves.chat.presentation.chat_list.ChatListViewModel
import com.ruialves.chat.presentation.chat_list_detail.ChaListDetailViewModel
import com.ruialves.chat.presentation.create_chat.CreateChatViewModel
import com.ruialves.chat.presentation.manage_chat.ManageChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChaListDetailViewModel)
    viewModelOf(::CreateChatViewModel)
    viewModelOf(::ChatDetailViewModel)
    viewModelOf(::ManageChatViewModel)
}
