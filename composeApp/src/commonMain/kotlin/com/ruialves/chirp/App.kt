package com.ruialves.chirp

import androidx.compose.runtime.Composable
import com.ruialves.chirp.navigation.NavigationRoot
import com.ruialves.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    ChirpTheme {
        NavigationRoot()
    }
}
