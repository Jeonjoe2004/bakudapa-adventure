package com.bakudapa.adventure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bakudapa.adventure.feature.feed.ui.CommentsScreen
import com.bakudapa.adventure.feature.feed.ui.CreatePostScreen
import com.bakudapa.adventure.feature.feed.ui.FeedScreen

fun NavGraphBuilder.feedNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Feed.route,
        route = "feed_root"
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToComments = { postId ->
                    navController.navigate(Screen.Comments.withArgs(postId))
                },
                onNavigateToCreatePost = {
                    navController.navigate(Screen.CreatePost.route)
                }
            )
        }

        composable(Screen.CreatePost.route) {
            CreatePostScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Comments.route + "/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            CommentsScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
