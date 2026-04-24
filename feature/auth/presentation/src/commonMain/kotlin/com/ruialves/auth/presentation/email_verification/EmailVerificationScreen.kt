package com.ruialves.auth.presentation.email_verification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import chirp.feature.auth.presentation.generated.resources.Res
import chirp.feature.auth.presentation.generated.resources.close
import chirp.feature.auth.presentation.generated.resources.email_verified_failed
import chirp.feature.auth.presentation.generated.resources.email_verified_failed_desc
import chirp.feature.auth.presentation.generated.resources.email_verified_successfully
import chirp.feature.auth.presentation.generated.resources.email_verified_sucessfully_desc
import chirp.feature.auth.presentation.generated.resources.login
import chirp.feature.auth.presentation.generated.resources.verifying_account
import com.ruialves.core.designsystem.components.brand.ChirpFailureIcon
import com.ruialves.core.designsystem.components.brand.ChirpSuccessIcon
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.buttons.ChirpButtonStyle
import com.ruialves.core.designsystem.components.layouts.ChirpAdaptiveResultLayout
import com.ruialves.core.designsystem.components.layouts.ChirpSimpleResultLayout
import com.ruialves.core.designsystem.components.layouts.ChirpSnackbarScaffold
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.VerticalSpacer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationRoot(
    viewModel: EmailVerificationViewModel = koinViewModel(),
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EmailVerificationScreen(
        state = state,
        onAction = { action ->
            when(action) {
                EmailVerificationAction.OnCloseClick,
                EmailVerificationAction.OnLoginClick -> onLoginClick()
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun EmailVerificationScreen(
    state: EmailVerificationState,
    onAction: (EmailVerificationAction) -> Unit,
) {
    ChirpSnackbarScaffold {
        ChirpAdaptiveResultLayout {
            when {
                state.isVerifying -> VerifyingContent(
                    modifier = Modifier.fillMaxWidth()
                )

                state.isVerified -> VerifiedContent {
                    onAction(EmailVerificationAction.OnLoginClick)
                }

                else -> VerifiedFailedContent {
                    onAction(EmailVerificationAction.OnCloseClick)
                }
            }
        }
    }
}

@Composable
private fun VerifyingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .heightIn(min = 200.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.verifying_account),
            color = MaterialTheme.colorScheme.extended.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun VerifiedContent(modifier: Modifier = Modifier, onLoginClick: () -> Unit) {
    ChirpSimpleResultLayout(
        title = stringResource(Res.string.email_verified_successfully),
        description = stringResource(Res.string.email_verified_sucessfully_desc),
        icon = { ChirpSuccessIcon() },
        primaryButton = {
            ChirpButton(
                text = stringResource(Res.string.login),
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
    )
}

@Composable
private fun VerifiedFailedContent(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    ChirpSimpleResultLayout(
        title = stringResource(Res.string.email_verified_failed),
        description = stringResource(Res.string.email_verified_failed_desc),
        icon = {
            VerticalSpacer(32.dp)
            ChirpFailureIcon()
            VerticalSpacer(32.dp)
        },
        primaryButton = {
            ChirpButton(
                text = stringResource(Res.string.close),
                onClick = onCloseClick,
                modifier = Modifier.fillMaxWidth(),
                style = ChirpButtonStyle.SECONDARY
            )
        },
    )
}


@Preview
@Composable
private fun VerifyingPreview() {
    ChirpTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(
                isVerifying = true
            ),
            onAction = {}
        )
    }
}


@Preview
@Composable
private fun VerifiedPreview() {
    ChirpTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(
                isVerified = true
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun VerifiedFailedPreview() {
    ChirpTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(
                isVerified = false
            ),
            onAction = {}
        )
    }
}
