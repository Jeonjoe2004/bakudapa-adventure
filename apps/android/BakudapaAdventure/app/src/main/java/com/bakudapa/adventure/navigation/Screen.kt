package com.bakudapa.adventure.navigation

/**
 * Sealed class representing all available screens in the application.
 */
sealed class Screen(val route: String) {
    // Auth Graph root
    object Auth : Screen("auth_graph")

    // Auth Screens
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object VerifyEmail : Screen("verify_email")
    object ProfileCompletion : Screen("profile_completion")

    // Main App Screens
    object Home : Screen("home")
    object Map : Screen("map")
    object Tracking : Screen("tracking")
    object Feed : Screen("feed")
    object CreatePost : Screen("create_post")
    object Comments : Screen("comments")
    object Chat : Screen("chat")
    object ChatRoom : Screen("chat_room")
    object Profile : Screen("profile")
    object Emergency : Screen("emergency")
    object Badge : Screen("badge")
    object Settings : Screen("settings")
    object MountainDetail : Screen("mountain_detail/{mountainId}") {
        fun createRoute(mountainId: String) = "mountain_detail/$mountainId"
    }
    object TrailDetail : Screen("trail_detail/{trailId}") {
        fun createRoute(trailId: String) = "trail_detail/$trailId"
    }

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
