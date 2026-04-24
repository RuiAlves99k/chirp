package com.ruialves.chirp

sealed interface MainEvent {
    data object OnSessionExpired: MainEvent
}
