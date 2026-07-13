package com.bakudapa.adventure.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Root Navigation Graph for the application.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            // Feature implementation will go here
        }
        
        composable(Screen.Auth.route) {
            // Feature implementation will go here
        }

        composable(Screen.Map.route) {
            // Feature implementation will go here
        }

        // Additional feature routes...
    }
}
