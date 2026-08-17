package com.asitkg.bmitracker.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable(Routes.PROFILES) { Placeholder("Profiles", "Phase 7") }
        composable(Routes.PROFILE_EDIT) { Placeholder("Edit profile", "Phase 7") }
    }
}

/** Temporary stand-in for destinations whose screens are not built yet. */
@Composable
private fun Placeholder(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
