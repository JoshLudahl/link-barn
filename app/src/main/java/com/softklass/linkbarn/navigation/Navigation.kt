package com.softklass.linkbarn.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softklass.linkbarn.ui.main.MainScreen
import com.softklass.linkbarn.ui.main.MainViewModel
import kotlinx.serialization.Serializable

@Serializable
private sealed interface Navigation {
    @Serializable
    data object Main : Navigation

    @Serializable
    data object Settings : Navigation
}

@Composable
fun AppNavHost(
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Navigation.Main,
        modifier = Modifier,
    ) {
        val animationTween = 350
        composable<Navigation.Main>(
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(animationTween),
                )
            },
            enterTransition = {
                when (initialState.destination.route) {
                    Navigation.Settings.toString() -> {
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
            )
        }
    }
}
