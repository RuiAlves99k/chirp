package com.ruialves.auth.presentation.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.create_account
import chirp.feature.auth.presentation.generated.resources.email
import chirp.feature.auth.presentation.generated.resources.email_placeholder
import chirp.feature.auth.presentation.generated.resources.forgot_password
import chirp.feature.auth.presentation.generated.resources.login
import chirp.feature.auth.presentation.generated.resources.password
import chirp.feature.auth.presentation.generated.resources.welcome_back
import com.ruialves.core.designsystem.components.brand.ChirpBrandLogo
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.buttons.ChirpButtonStyle
import com.ruialves.core.designsystem.components.layouts.ChirpAdaptiveFormLayout
import com.ruialves.core.designsystem.components.textfields.ChirpPasswordTextField
import com.ruialves.core.designsystem.components.textfields.ChirpTextField
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.presentation.util.ObserveAsEvents
import com.ruialves.core.presentation.util.VerticalSpacer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    ChirpAdaptiveFormLayout(
        headerText = stringResource(Res.string.welcome_back),
        errorText = state.error?.asString(),
        logo = { ChirpBrandLogo() },
        modifier = Modifier.fillMaxSize()
    ) {
        ChirpTextField(
            title = stringResource(Res.string.email),
            state = state.emailTextFieldState,
            placeholder = stringResource(Res.string.email_placeholder),
            keyboardType = KeyboardType.Email,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        VerticalSpacer(16.dp)
        ChirpPasswordTextField(
            title = stringResource(Res.string.password),
            state = state.passwordTextFieldState,
            placeholder = stringResource(Res.string.password),
            modifier = Modifier.fillMaxWidth(),
            onToggleVisibilityClick = {
                onAction(LoginAction.OnTogglePasswordVisibility)
            },
            isPasswordVisible = state.isPasswordVisible,
        )
        VerticalSpacer(8.dp)
        ChirpButton(
            text = stringResource(Res.string.forgot_password), onClick = {
                onAction(LoginAction.OnForgotPasswordClick)
            }, style = ChirpButtonStyle.TEXT, modifier = Modifier.align(Alignment.End)
        )
        VerticalSpacer(16.dp)
        ChirpButton(
            text = stringResource(Res.string.login),
            onClick = {
                onAction(LoginAction.OnLoginClick)
            },
            isLoading = state.isLoggingIn,
            enabled = state.canLogin,
            modifier = Modifier.fillMaxWidth(),
        )
        VerticalSpacer(8.dp)
        ChirpButton(
            text = stringResource(Res.string.create_account),
            onClick = {
                onAction(LoginAction.OnSignUpClick)
            },
            style = ChirpButtonStyle.SECONDARY,
            modifier = Modifier.fillMaxWidth(),
        )

    }
}

@Preview
@Composable
private fun Preview() {
    ChirpTheme {
        LoginScreen(
            state = LoginState(), onAction = {})
    }
}

@Preview
@Composable
private fun DarkPreview() {
    ChirpTheme(darkTheme = true) {
        LoginScreen(
            state = LoginState(), onAction = {})
    }
}
