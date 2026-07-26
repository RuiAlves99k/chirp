package com.ruialves.chat.presentation.chat_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruialves.chat.domain.chat.ChatRepository
import com.ruialves.chat.presentation.mappers.toUi
import com.ruialves.core.domain.analytics.AnalyticsAdapter
import com.ruialves.core.domain.auth.SessionStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val sessionStorage: SessionStorage,
    private val analyticsAdapter: AnalyticsAdapter,
    private val repository: ChatRepository,
) : ViewModel() {

    private var hasLoadedInitialData = false

    private val channelEvent = Channel<ChatListEvent>()
    val events = channelEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ChatListState())
    val state = combine(
        _state,
        repository.getChats(),
        sessionStorage.observeAuthInfo()
    ) { currentState, chats, authInfo ->
        if (authInfo == null) {
            return@combine ChatListState()
        }
        currentState.copy(
            chats = chats.map { it.toUi(authInfo.user.id) },
            localParticipant = authInfo.user.toUi()
        )
    }.onStart {
        if (!hasLoadedInitialData) {
            loadChats()
            analyticsAdapter.trackEvent(
                "screen_view",
                mapOf("screen" to "chat_list")
            )
            hasLoadedInitialData = true
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ChatListState()
    )

    fun onAction(action: ChatListAction) {
        when (action) {
            is ChatListAction.OnChatClick -> {
                _state.update { it.copy(
                    selectedChatId = action.chat.id
                ) }
            }
            else -> Unit
        }
    }


    private fun loadChats() {
        viewModelScope.launch {
            repository.fetchChats()
        }
    }

}
