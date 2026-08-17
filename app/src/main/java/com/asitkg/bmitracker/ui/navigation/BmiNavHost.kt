package com.asitkg.bmitracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.asitkg.bmitracker.ui.profiles.ProfileEditScreen
import com.asitkg.bmitracker.ui.profiles.ProfilesScreen
import com.asitkg.bmitracker.ui.auth.forgot.ForgotPasswordScreen
import com.asitkg.bmitracker.ui.auth.login.LoginScreen
import com.asitkg.bmitracker.ui.auth.signup.SignUpScreen
import com.asitkg.bmitracker.ui.dashboard.DashboardScreen
import com.asitkg.bmitracker.ui.onboarding.UserDetailsScreen
import com.asitkg.bmitracker.ui.settings.SettingsScreen
import com.asitkg.bmitracker.ui.splash.SplashScreen
import com.asitkg.bmitracker.ui.splash.StartDestination

@Composable
fun BmiNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onDecided = { destination ->
                    val route = when (destination) {
                        StartDestination.Login -> Routes.LOGIN
                        StartDestination.Onboarding -> Routes.ONBOARDING
                        StartDestination.Dashboard -> Routes.DASHBOARD
                        StartDestination.Undecided -> return@SplashScreen
                    }
                    navController.navigate(route) {
                        // The splash screen must never be reachable via back.
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onSignedIn = {
                    navController.navigate(Routes.SPLASH) {
                        // Route through splash again so it decides between
                        // onboarding and dashboard in one place.
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(Routes.SIGNUP) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onSignedUp = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ONBOARDING) {
            UserDetailsScreen(
                onSaved = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onAddDetails = { navController.navigate(Routes.ONBOARDING) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onOpenProfiles = { navController.navigate(Routes.PROFILES) },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        // Clear the whole back stack: signing out must not leave
                        // another account's screens reachable via back.
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.PROFILES) {
            ProfilesScreen(
                onBack = { navController.popBackStack() },
                onAddProfile = { navController.navigate(Routes.ONBOARDING) },
                onEditProfile = { id -> navController.navigate(Routes.profileEdit(id)) },
                onProfileSelected = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.PROFILE_EDIT,
            arguments = listOf(navArgument(Routes.PROFILE_ID_ARG) { type = NavType.LongType }),
        ) {
            ProfileEditScreen(onBack = { navController.popBackStack() })
        }
    }
}
