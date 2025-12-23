package com.softklass.linkbarn.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softklass.linkbarn.ui.categories.CategoriesScreen
import com.softklass.linkbarn.ui.dashboard.DashboardScreen
import com.softklass.linkbarn.ui.main.MainScreen
import com.softklass.linkbarn.ui.main.MainViewModel
import com.softklass.linkbarn.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Main : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Categories : Screen

    @Serializable
    data object Dashboard : Screen
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Main,
        modifier = Modifier,
    ) {
        val animationTween = 350
        composable<Screen.Main>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween),
                )
            },
            enterTransition = {
                when (initialState.destination.route) {
                    Screen.Settings.toString() -> {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween),
                        )
                    }

                    Screen.Categories.toString() -> {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween),
                        )
                    }

                    Screen.Dashboard.toString() -> {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween),
                        )
                    }

                    else -> {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(animationTween),
                        )
                    }
                }
            },
        ) {
            MainScreen(
                viewModel = hiltViewModel<MainViewModel>(),
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings)
                },
                onNavigateToCategories = {
                    navController.navigate(Screen.Categories)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard)
                },
            )
        }

        composable<Screen.Settings>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(animationTween),
                )
            },
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween),
                )
            },
        ) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<Screen.Categories>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(animationTween),
                )
            },
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween),
                )
            },
        ) {
            CategoriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable<Screen.Dashboard>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(animationTween),
                )
            },
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween),
                )
            },
        ) {
            DashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
