package com.ruialves.auth.presentation.forgot_password

sealed interface ForgotPasswordAction {
    data object OnSubmitClick: ForgotPasswordAction
}
