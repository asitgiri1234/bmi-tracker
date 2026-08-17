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
import com.asitkg.bmitracker.ui.onboarding.UserDetailsScreen

/**
 * Navigation skeleton.
 *
 * Destinations are wired up front so later phases only have to swap each
 * [Placeholder] for the real screen without touching routing.
 */
@Composable
fun BmiNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        // Temporary while auth is unbuilt: start on the details form so the
        // screen is reachable. Phase 1 restores SPLASH as the entry point.
        startDestination = Routes.ONBOARDING,
    ) {
        composable(Routes.SPLASH) { Placeholder("Splash", "Phase 1 — routes on persisted auth state") }

        composable(Routes.LOGIN) { Placeholder("Login", "Phase 1") }
        composable(Routes.SIGNUP) { Placeholder("Sign up", "Phase 1") }
        composable(Routes.FORGOT_PASSWORD) { Placeholder("Reset password", "Phase 1") }

        composable(Routes.ONBOARDING) {
            UserDetailsScreen(
                onSaved = {
                    navController.navigate(Routes.DASHBOARD) {
                        // Onboarding is one-time; back must not return to it.
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) { Placeholder("Dashboard", "Phase 4 + 6") }
        composable(Routes.SETTINGS) { Placeholder("Settings", "Phase 5") }
        composable(Routes.PROFILES) { Placeholder("Profiles", "Phase 7") }
        composable(Routes.PROFILE_EDIT) { Placeholder("Edit profile", "Phase 7") }
    }
}

/** Temporary stand-in so the navigation graph is runnable before screens exist. */
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
