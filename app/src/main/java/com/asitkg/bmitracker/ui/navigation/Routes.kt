package com.asitkg.bmitracker.ui.navigation

/**
 * Every navigable destination in the app.
 *
 * Grouped into an auth graph and a main graph: [Splash] decides which of the
 * two the user lands in based on persisted Firebase auth state, so a returning
 * user never sees the login screen again.
 */
object Routes {
    const val SPLASH = "splash"

    // Auth graph
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"

    // Main graph
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
    const val PROFILES = "profiles"
    const val PROFILE_EDIT = "profile_edit"
}
