package com.ruialves.chirp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruialves.core.data.auth.DataStoreSessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val sessionStorage: DataStoreSessionStorage
): ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
            _state.update { it.copy(
                isCheckingAuth = false,
                isLoggedIn = authInfo != null
            ) }
        }
    }
}
