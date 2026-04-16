package com.ruialves.core.designsystem.components.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ruialves.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChirpSnackbarScaffold(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars
            .union(WindowInsets.displayCutout)
            .union(WindowInsets.ime),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 24.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ){
            content()
        }
    }
}

@Composable
@Preview
fun ChirpSnackbarScaffoldPreview() {
    ChirpTheme {
        ChirpSnackbarScaffold(
            snackbarHostState = SnackbarHostState(),
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Error with network!",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
