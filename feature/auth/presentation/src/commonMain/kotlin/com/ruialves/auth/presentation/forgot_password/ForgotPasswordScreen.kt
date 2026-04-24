package com.ruialves.auth.presentation.forgot_password

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.email
import chirp.feature.auth.presentation.generated.resources.email_placeholder
import chirp.feature.auth.presentation.generated.resources.forgot_password
import chirp.feature.auth.presentation.generated.resources.forgot_password_email_sent_successfully
import chirp.feature.auth.presentation.generated.resources.submit
import com.ruialves.core.designsystem.components.brand.ChirpBrandLogo
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.layouts.ChirpAdaptiveFormLayout
import com.ruialves.core.designsystem.components.layouts.ChirpSnackbarScaffold
import com.ruialves.core.designsystem.components.textfields.ChirpTextField
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.VerticalSpacer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ForgotPasswordRoot(
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onAction: (ForgotPasswordAction) -> Unit,
) {
    ChirpSnackbarScaffold {
        ChirpAdaptiveFormLayout(
            headerText = stringResource(Res.string.forgot_password),
            errorText = state.errorText?.asString(),
            logo = { ChirpBrandLogo() }
        ) {
            ChirpTextField(
                state = state.emailTextFieldState,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                isError = state.errorText != null,
                supportingText = state.errorText?.asString(),
                keyboardType = KeyboardType.Email,
            )
            VerticalSpacer(16.dp)
            ChirpButton(
                text = stringResource(Res.string.submit),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.canSubmit,
                isLoading = state.isLoading,
                onClick = { onAction(ForgotPasswordAction.OnSubmitClick) }
            )
            VerticalSpacer(16.dp)
            if (state.isEmailSentSuccessfully) {
                Text(
                    text = stringResource(Res.string.forgot_password_email_sent_successfully),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.extended.success,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ChirpTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordState(),
            onAction = {}
        )
    }
}
