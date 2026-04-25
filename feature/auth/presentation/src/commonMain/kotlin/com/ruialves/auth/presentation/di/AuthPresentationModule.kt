package com.ruialves.auth.presentation.di

import com.ruialves.auth.presentation.email_verification.EmailVerificationViewModel
import com.ruialves.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.ruialves.auth.presentation.login.LoginViewModel
import com.ruialves.auth.presentation.register.RegisterViewModel
import com.ruialves.auth.presentation.register_success.RegisterSuccessViewModel
import com.ruialves.auth.presentation.reset_password.ResetPasswordViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::RegisterSuccessViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::ResetPasswordViewModel)
}
