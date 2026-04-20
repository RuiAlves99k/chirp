package com.ruialves.auth.presentation.register_success

import com.ruialves.core.presentation.util.UiText

data class RegisterSuccessState(
    val registerEmail: String = "",
    val isResendingVerificationEmail: Boolean = false,
    val secondaryError: UiText? = null,
)
