package com.momoko.tgramtvunlimited.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.momoko.tgramtvunlimited.ui.screens.chat.ChatListScreen
import com.momoko.tgramtvunlimited.ui.screens.chat.ChatScreen
import com.momoko.tgramtvunlimited.ui.screens.login.LoginScreen
import com.momoko.tgramtvunlimited.ui.screens.media.MediaPlayerScreen
import com.momoko.tgramtvunlimited.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: Long) = "chat/$chatId"
    }
    object MediaPlayer : Screen("media/{messageId}") {
        fun createRoute(messageId: Long) = "media/$messageId"
    }
    object Settings : Screen("settings")
}

@Composable
fun TgramNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.ChatList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate(Screen.Chat.createRoute(chatId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.LongType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
            ChatScreen(
                chatId = chatId,
                onMediaClick = { messageId ->
                    navController.navigate(Screen.MediaPlayer.createRoute(messageId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MediaPlayer.route,
            arguments = listOf(navArgument("messageId") { type = NavType.LongType })
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getLong("messageId") ?: 0L
            MediaPlayerScreen(
                messageId = messageId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}