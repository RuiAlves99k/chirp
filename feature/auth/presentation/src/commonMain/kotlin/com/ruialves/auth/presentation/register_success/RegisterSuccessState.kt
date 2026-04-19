package com.ruialves.auth.presentation.register_success

data class RegisterSuccessState(
    val registerEmail: String = "",
    val isResendingVerificationEmail: Boolean = false,
)
