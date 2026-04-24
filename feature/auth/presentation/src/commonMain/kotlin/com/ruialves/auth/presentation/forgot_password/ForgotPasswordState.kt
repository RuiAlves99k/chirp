package com.ruialves.auth.presentation.forgot_password

import androidx.compose.foundation.text.input.TextFieldState
import com.ruialves.core.presentation.util.UiText

data class ForgotPasswordState(
    val emailTextFieldState: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val isLoading: Boolean = false,
    val errorText: UiText? = null,
    val isEmailSentSuccessfully: Boolean = false,
    val canSubmit: Boolean = false,
)
