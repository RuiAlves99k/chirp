package com.ruialves.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.error_account_exists
import chirp.feature.auth.presentation.generated.resources.error_invalid_email
import chirp.feature.auth.presentation.generated.resources.error_invalid_password
import chirp.feature.auth.presentation.generated.resources.error_invalid_username
import com.ruialves.auth.domain.EmailValidator
import com.ruialves.core.domain.auth.AuthService
import com.ruialves.core.domain.util.DataError
import com.ruialves.core.domain.util.onFailure
import com.ruialves.core.domain.util.onSuccess
import com.ruialves.core.domain.validation.PasswordValidator
import com.ruialves.core.presentation.util.UiText
import com.ruialves.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(RegisterState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                /** Load initial data here **/
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RegisterState()
        )

    fun onAction(action: RegisterAction) {
        when (action) {
            RegisterAction.OnLoginClick -> validateFormInputs()
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                _state.update { it.copy(
                    isPasswordVisible = !it.isPasswordVisible
                ) }
            }
            else -> Unit
        }
    }

    private fun register() {
        if (!validateFormInputs()) {
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(
                isRegistering = true
            ) }

            val currentState = state.value
            val email = currentState.emailTextState.text.toString().trim()
            val username = currentState.usernameTextState.text.toString().trim()
            val password = currentState.passwordTextState.text.toString()

            authService
                .register(
                    email = email,
                    username = username,
                    password = password
                ).onSuccess {
                    _state.update { it.copy(
                        isRegistering = false
                    ) }

                }
                .onFailure { error ->
                    val registrationError = when(error) {
                        DataError.Remote.CONFLICT -> UiText.Resource(Res.string.error_account_exists)
                        else -> error.toUiText()
                    }
                    _state.update { it.copy(
                        isRegistering = false,
                        registrationError = registrationError
                    ) }

                }
        }
    }

    private fun clearAllTextFieldsErrors() {
        _state.update {
            it.copy(
                emailError = null,
                usernameError = null,
                passwordError = null,
                registrationError = null
            )
        }
    }


    private fun validateFormInputs(): Boolean {
        clearAllTextFieldsErrors()

        val currentState = state.value
        val email = currentState.emailTextState.text.toString().trim()
        val username = currentState.usernameTextState.text.toString().trim()
        val password = currentState.passwordTextState.text.toString()

        val isEmailValid = EmailValidator.validate(email)
        val passwordValidationState = PasswordValidator.validate(password)
        val isUsernameValid = username.length in 3..25 && username.none { it.isWhitespace() }

        val emailError = if (!isEmailValid) {
            UiText.Resource(Res.string.error_invalid_email)
        } else null
        val usernameError = if (!isUsernameValid) {
            UiText.Resource(Res.string.error_invalid_username)
        } else null
        val passwordError = if (!passwordValidationState.isValidPassword) {
            UiText.Resource(Res.string.error_invalid_password)
        } else null

        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordError = passwordError
            )
        }

        return isEmailValid && passwordValidationState.isValidPassword && isUsernameValid
    }

}
