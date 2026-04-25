package com.ruialves.auth.presentation.reset_password

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.password
import chirp.feature.auth.presentation.generated.resources.password_hint
import chirp.feature.auth.presentation.generated.resources.reset_password_successfully
import chirp.feature.auth.presentation.generated.resources.set_new_password
import chirp.feature.auth.presentation.generated.resources.submit
import com.ruialves.core.designsystem.components.brand.ChirpBrandLogo
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.layouts.ChirpAdaptiveFormLayout
import com.ruialves.core.designsystem.components.layouts.ChirpSnackbarScaffold
import com.ruialves.core.designsystem.components.textfields.ChirpPasswordTextField
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.VerticalSpacer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ResetPasswordRoot(
    viewModel: ResetPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordState,
    onAction: (ResetPasswordAction) -> Unit,
) {
    ChirpSnackbarScaffold {
        ChirpAdaptiveFormLayout(
            headerText = stringResource(Res.string.set_new_password),
            errorText = state.errorText?.asString(),
            logo = { ChirpBrandLogo() },
        ) {
            ChirpPasswordTextField(
                state = state.passwordTextState,
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.password),
                placeholder = stringResource(Res.string.password),
                supportingText = stringResource(Res.string.password_hint),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ResetPasswordAction.OnPasswordVisibilityClick)
                }
            )
            VerticalSpacer(16.dp)
            ChirpButton(
                text = stringResource(Res.string.submit),
                onClick = {
                    onAction(ResetPasswordAction.OnSubmitClick)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading && state.canSubmit,
                isLoading = state.isLoading
            )
            if (state.isResetSuccessful) {
                VerticalSpacer(8.dp)
                Text(
                    text = stringResource(Res.string.reset_password_successfully),
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
        ResetPasswordScreen(
            state = ResetPasswordState(),
            onAction = {}
        )
    }
}
