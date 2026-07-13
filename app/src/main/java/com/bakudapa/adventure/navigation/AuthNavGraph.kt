package com.bakudapa.adventure.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.bakudapa.adventure.feature.auth.ui.forgot.ForgotPasswordScreen
import com.bakudapa.adventure.feature.auth.ui.login.LoginScreen
import com.bakudapa.adventure.feature.auth.ui.onboarding.OnboardingScreen
import com.bakudapa.adventure.feature.auth.ui.profile.ProfileCompletionScreen
import com.bakudapa.adventure.feature.auth.ui.register.RegisterScreen
import com.bakudapa.adventure.feature.auth.ui.splash.SplashScreen
import com.bakudapa.adventure.feature.auth.ui.verify.VerifyEmailScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Splash.route,
        route = Screen.Auth.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToVerifyEmail = { navController.navigate(Screen.VerifyEmail.route) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.VerifyEmail.route) {
            VerifyEmailScreen(
                onNavigateToProfileCompletion = {
                    navController.navigate(Screen.ProfileCompletion.route) {
                        popUpTo(Screen.VerifyEmail.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProfileCompletion.route) {
            ProfileCompletionScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
