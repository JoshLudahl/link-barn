package com.softklass.linkbarn.navigation

/**
 * Composable Destinations should be named after the Screen object in ScreenScreen format.
 * Example: For Home it should be HomeScreen.
 *
 */
sealed class Screen(
    val route: String
) {
    data object Home: Screen("home_screen")
    data object Settings: Screen("settings_screen")
}
