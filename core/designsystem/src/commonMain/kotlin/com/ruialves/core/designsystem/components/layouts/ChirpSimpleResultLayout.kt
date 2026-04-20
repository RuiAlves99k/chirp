package com.ruialves.core.designsystem.components.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ruialves.core.designsystem.components.brand.ChirpSuccessIcon
import com.ruialves.core.designsystem.components.buttons.ChirpButton
import com.ruialves.core.designsystem.components.buttons.ChirpButtonStyle
import com.ruialves.core.designsystem.theme.ChirpTheme
import com.ruialves.core.designsystem.theme.extended
import com.ruialves.core.presentation.util.VerticalSpacer
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpSimpleResultLayout(
    title: String,
    description: String,
    icon: @Composable ColumnScope.() -> Unit,
    primaryButton: @Composable () -> Unit,
    secondaryButton: @Composable (() -> Unit)? = null,
    secondaryError: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -(25).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(8.dp)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.extended.textSecondary,
                textAlign = TextAlign.Center
            )
            VerticalSpacer(24.dp)
            primaryButton()
            if (secondaryButton != null){
                VerticalSpacer(8.dp)
                secondaryButton()
                if (secondaryError != null) {
                    VerticalSpacer(6.dp)
                    Text(
                        text = secondaryError,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
@Preview(
    showBackground = true
)
private fun ChirpSimpleSuccessLayoutPreview() {
    ChirpTheme {
        ChirpSimpleResultLayout(
            title = "Hello world!",
            description = "Test description",
            icon = {
                ChirpSuccessIcon()
            },
            primaryButton = {
                ChirpButton(
                    text = "Log In",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                )
            },
            secondaryButton = {
                ChirpButton(
                    text = "Resend verification email",
                    onClick = {},
                    style = ChirpButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}
