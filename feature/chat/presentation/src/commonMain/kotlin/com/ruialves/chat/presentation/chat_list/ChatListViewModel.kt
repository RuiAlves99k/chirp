package com.ruialves.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruialves.core.domain.auth.SessionStorage
import com.ruialves.core.domain.util.onFailure
import com.ruialves.core.domain.util.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val channelEvent = Channel<ChatListEvent>()
    val events = channelEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ChatListState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadUser()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChatListState()
        )

    fun onAction(action: ChatListAction) {
        when (action) {
            ChatListAction.Logout -> {
                viewModelScope.launch {
                    sessionStorage.set(null)
                }
            }
        }
    }

    private fun loadUser(){
        viewModelScope.launch {
            sessionStorage
                .observeAuthInfo()
                .collectLatest { authInfo ->
                    _state.update { it.copy(
                        username = authInfo?.user?.username
                    ) }
                }
        }
    }

}
