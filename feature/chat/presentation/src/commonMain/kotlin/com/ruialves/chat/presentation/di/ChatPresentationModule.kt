package com.ruialves.chat.presentation.di

import com.ruialves.chat.presentation.chat_list.ChatListViewModel
import com.ruialves.chat.presentation.chat_list_detail.ChaListDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatPresentationModule = module {
    viewModelOf(::ChatListViewModel)
    viewModelOf(::ChaListDetailViewModel)
}
