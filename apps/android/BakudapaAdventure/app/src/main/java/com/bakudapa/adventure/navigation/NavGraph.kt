package com.bakudapa.adventure.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bakudapa.adventure.feature.badge.ui.BadgeScreen
import com.bakudapa.adventure.feature.emergency.ui.EmergencyScreen
import com.bakudapa.adventure.feature.home.ui.HomeScreen
import com.bakudapa.adventure.feature.leaderboard.ui.LeaderboardScreen
import com.bakudapa.adventure.feature.map.ui.MapScreen
import com.bakudapa.adventure.feature.mountain.ui.MountainDetailScreen
import com.bakudapa.adventure.feature.mountain.ui.MountainListScreen
import com.bakudapa.adventure.feature.profile.ui.EditProfileScreen
import com.bakudapa.adventure.feature.profile.ui.ProfileScreen
import com.bakudapa.adventure.feature.profile.ui.UserProfileScreen
import com.bakudapa.adventure.feature.settings.ui.SettingsScreen
import com.bakudapa.adventure.feature.story.ui.StoryViewerScreen
import com.bakudapa.adventure.feature.trail.ui.TrailDetailScreen
import com.bakudapa.adventure.feature.trail.ui.TrailUploadScreen
import com.bakudapa.adventure.feature.tracking.ui.TrackingScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.route,
    modifier: Modifier = Modifier,
    auth: FirebaseAuth
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth sub-graph
        authNavGraph(navController)

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToMountainList = { navController.navigate(Screen.MountainList.route) },
                onNavigateToMountainDetail = { id ->
                    navController.navigate(Screen.MountainDetail.createRoute(id))
                },
                onNavigateToTrailDetail = { id ->
                    navController.navigate(Screen.TrailDetail.createRoute(id))
                },
                onNavigateToPostDetail = { id ->
                    navController.navigate(Screen.PostDetail.createRoute(id))
                }
            )
        }

        // Mountain List
        composable(Screen.MountainList.route) {
            MountainListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.MountainDetail.createRoute(id))
                },
                onNavigateToMap = { navController.navigate(Screen.Map.route) }
            )
        }

        // Mountain Detail
        composable(
            route = Screen.MountainDetail.route,
            arguments = listOf(
                navArgument("mountainId") { type = NavType.StringType }
            )
        ) {
            MountainDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrail = {
                    navController.navigate(Screen.TrailDetail.createRoute(it))
                },
                onNavigateToMap = { lat, lng, name ->
                    navController.navigate(Screen.Map.route)
                }
            )
        }

        // Feed sub-graph (FeedScreen + CreatePost + Comments)
        feedNavGraph(navController)

        // Chat sub-graph (ChatList + ChatRoom)
        chatNavGraph(navController, auth)

        // Map
        composable(Screen.Map.route) {
            MapScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Tracking
        composable(Screen.Tracking.route) {
            TrackingScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreatePost = { content ->
                    navController.navigate(Screen.CreatePost.route)
                }
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToUserProfile = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                }
            )
        }

        // Edit Profile
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        // Badge
        composable(Screen.Badge.route) {
            BadgeScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Leaderboard
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateToUser = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // User Profile (other user)
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Emergency
        composable(Screen.Emergency.route) {
            EmergencyScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Trail Detail
        composable(
            route = Screen.TrailDetail.route,
            arguments = listOf(navArgument("trailId") { type = NavType.StringType })
        ) {
            TrailDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTracking = {
                    navController.navigate(Screen.Tracking.route)
                }
            )
        }

        // Trail Upload
        composable(Screen.TrailUpload.route) {
            TrailUploadScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Story Viewer
        composable(
            route = Screen.StoryViewer.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            StoryViewerScreen(
                userId = userId,
                onClose = { navController.popBackStack() }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
