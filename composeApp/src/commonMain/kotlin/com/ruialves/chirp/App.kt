package com.ruialves.chirp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ruialves.chirp.navigation.DeepLinkListener
import com.ruialves.chirp.navigation.NavigationRoot
import com.ruialves.core.designsystem.theme.ChirpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    DeepLinkListener(navController)

    ChirpTheme {
        NavigationRoot(navController)
    }
}
