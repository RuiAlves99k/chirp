package com.ruialves.auth.presentation.reset_password

sealed interface ResetPasswordAction {
    data object OnSubmitClick: ResetPasswordAction
    data object OnPasswordVisibilityClick: ResetPasswordAction
}
