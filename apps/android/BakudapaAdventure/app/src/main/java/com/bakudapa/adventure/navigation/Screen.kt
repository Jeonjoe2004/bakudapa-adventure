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
    object MountainList : Screen("mountain_list")
    object Feed : Screen("feed")
    object CreatePost : Screen("create_post")
    object Comments : Screen("comments")
    object Chat : Screen("chat")
    object ChatRoom : Screen("chat_room")
    object Profile : Screen("profile")
    object Emergency : Screen("emergency")
    object Badge : Screen("badge")
    object Settings : Screen("settings")
    object Leaderboard : Screen("leaderboard")
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
    object MountainDetail : Screen("mountain_detail/{mountainId}") {
        fun createRoute(mountainId: String) = "mountain_detail/$mountainId"
    }
    object TrailDetail : Screen("trail_detail/{trailId}") {
        fun createRoute(trailId: String) = "trail_detail/$trailId"
    }
    object EditProfile : Screen("edit_profile")
    object TrailUpload : Screen("trail_upload")
    object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    // Gear Checklist
    object GearChecklist : Screen("gear_checklist/{mountainId}/{mountainName}") {
        fun createRoute(mountainId: String, mountainName: String) = "gear_checklist/$mountainId/$mountainName"
    }

    object CreateSummitLog : Screen("create_summit_log/{mountainId}/{mountainName}") {
        fun createRoute(mountainId: String, mountainName: String) = "create_summit_log/$mountainId/$mountainName"
    }

    // Stories
    object StoryViewer : Screen("story_viewer/{userId}") {
        fun createRoute(userId: String) = "story_viewer/$userId"
    }
    object CreateStory : Screen("create_story")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
