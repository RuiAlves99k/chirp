package com.ruialves.chirp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ruialves.auth.presentation.navigation.AuthGraphRoutes
import com.ruialves.auth.presentation.navigation.authGraph
import com.ruialves.chat.presentation.chat_list.ChatListRoute
import com.ruialves.chat.presentation.chat_list.ChatListScreenRoot

@Composable
fun NavigationRoot(navController: NavHostController, startDestination: Any) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            navController = navController,
            onLoginSuccess = {
                navController.navigate(ChatListRoute) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = true
                    }
                }
            }
        )
        composable<ChatListRoute> {
            ChatListScreenRoot(
                onLogout = {
                    navController.navigate(AuthGraphRoutes.Graph) {
                        popUpTo(ChatListRoute) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
