package com.softklass.linkbarn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softklass.linkbarn.ui.main.MainScreen
import com.softklass.linkbarn.ui.settings.SettingsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    AppNavHost(navController)
}

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
