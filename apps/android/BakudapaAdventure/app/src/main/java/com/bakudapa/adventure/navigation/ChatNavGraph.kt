package com.bakudapa.adventure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bakudapa.adventure.feature.chat.ui.ChatListScreen
import com.bakudapa.adventure.feature.chat.ui.ChatRoomScreen
import com.google.firebase.auth.FirebaseAuth

fun NavGraphBuilder.chatNavGraph(navController: NavHostController, auth: FirebaseAuth) {
    navigation(
        startDestination = Screen.Chat.route,
        route = "chat_root"
    ) {
        composable(Screen.Chat.route) {
            ChatListScreen(
                onNavigateToRoom = { roomId ->
                    navController.navigate(Screen.ChatRoom.withArgs(roomId))
                }
            )
        }
        
        composable(
            route = Screen.ChatRoom.route + "/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            ChatRoomScreen(
                roomId = roomId,
                onNavigateBack = { navController.popBackStack() },
                auth = auth
            )
        }
    }
}
