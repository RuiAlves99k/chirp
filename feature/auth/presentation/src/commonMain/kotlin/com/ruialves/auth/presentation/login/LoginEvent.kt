package com.ruialves.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}
