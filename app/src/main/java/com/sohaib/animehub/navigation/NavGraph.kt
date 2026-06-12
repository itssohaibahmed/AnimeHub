package com.sohaib.animehub.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sohaib.animehub.feature.anime.details.ANIME_DETAILS_ROUTE
import com.sohaib.animehub.feature.anime.details.AnimeDetailsScreen
import com.sohaib.animehub.feature.anime.details.createRouteAnimeDetails
import com.sohaib.animehub.feature.dashboard.DASHBOARD_ROUTE
import com.sohaib.animehub.feature.dashboard.DashboardScreen
import com.sohaib.animehub.feature.favourites.FavouriteScreen
import com.sohaib.animehub.feature.home.HomeScreen
import com.sohaib.animehub.feature.settings.SettingScreen
import com.sohaib.animehub.feature.splash.SPLASH_ROUTE
import com.sohaib.animehub.feature.splash.SplashScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE,
        modifier = modifier.fillMaxSize(),
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } }
    ) {

        composable(
            route = SPLASH_ROUTE
        ) {
            SplashScreen(
                navigateToDashboard = {
                    navController.navigate(DASHBOARD_ROUTE) {
                        popUpTo(SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = DASHBOARD_ROUTE
        ) {
            DashboardScreen(
                homeContent = {
                    HomeScreen(
                        onNavigateToDetailPage = {
                            navController.navigate(createRouteAnimeDetails(it))
                        }
                    )
                },
                favouriteContent = {
                    FavouriteScreen(
                        onNavigateToDetailPage = {
                            navController.navigate(createRouteAnimeDetails(it))
                        },
                    )
                },
                settingContent = { SettingScreen() }
            )
        }

        composable(
            route = ANIME_DETAILS_ROUTE,
            arguments = listOf(navArgument("animeId") { type = NavType.StringType })
        ) {
            val animeId = it.arguments?.getString("animeId")
            AnimeDetailsScreen(
                animeId = animeId,
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }
}