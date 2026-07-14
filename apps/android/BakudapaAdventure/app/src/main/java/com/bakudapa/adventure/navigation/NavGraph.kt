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
import com.bakudapa.adventure.feature.map.ui.MapScreen
import com.bakudapa.adventure.feature.profile.ui.ProfileScreen
import com.bakudapa.adventure.feature.mountain.ui.MountainDetailScreen
import com.bakudapa.adventure.feature.settings.ui.SettingsScreen
import com.bakudapa.adventure.feature.trail.ui.TrailDetailScreen
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
                onNavigateToMountainDetail = { id ->
                    navController.navigate(Screen.MountainDetail.createRoute(id))
                },
                onNavigateToTrailDetail = { id ->
                    navController.navigate(Screen.TrailDetail.createRoute(id))
                }
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
            TrackingScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEditProfile = { /* TODO: EditProfileScreen */ },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Badge
        composable(Screen.Badge.route) {
            BadgeScreen(onNavigateBack = { navController.popBackStack() })
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

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditProfile = { /* TODO */ },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
