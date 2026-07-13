package com.bakudapa.adventure.navigation

/**
 * Sealed class representing all available screens in the application.
 */
sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Map : Screen("map")
    object Tracking : Screen("tracking")
    object Feed : Screen("feed")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Emergency : Screen("emergency")
    object Badge : Screen("badge")
    object Settings : Screen("settings")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
