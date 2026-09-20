package no.uio.ifi.in2000.team39.in2000_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.contact.ContactScreen
import no.uio.ifi.in2000.team39.in2000_project.ui.favorite.FavoriteScreen
import no.uio.ifi.in2000.team39.in2000_project.ui.home.HomeScreen
import no.uio.ifi.in2000.team39.in2000_project.ui.map.MapScreen
import no.uio.ifi.in2000.team39.in2000_project.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast.WeatherForecastScreen

/**
 * Configures the navigation architecture for the application, defining routes and their associated screens.
 * This setup function is critical for managing the navigation flow throughout the app,
 * facilitating transitions with [navController] between different content areas such as home, map, settings, and weather forecast.
 */
@Composable
fun SetupNavHost(
    navController: NavHostController
) {
    // Define routes using string resources for better manageability and localization support.
    val homeScreenRoute = stringResource(id = R.string.home_screen_route)
    val mapScreenRoute = stringResource(id = R.string.map_screen_route)
    val settingsScreenRoute = stringResource(id = R.string.setting_screen_route)
    val weatherForecastScreenRoute =
        stringResource(id = R.string.weather_forecast_screen_route) + stringResource(
            id = R.string.weather_forecast_screen_route_args
        )
    val favoriteScreenRoute = stringResource(id = R.string.favorites_screen_route)
    val contactScreenRoute = stringResource(id = R.string.contact_screen_route)

    // Setup the navigation host, which acts as a container for navigating between composable screens.
    NavHost(
        navController = navController,
        startDestination = homeScreenRoute,
    ) {
        composable(homeScreenRoute) {
            HomeScreen(navController)
        }

        composable(mapScreenRoute) {
            MapScreen(navController)
        }

        composable(settingsScreenRoute) {
            SettingsScreen(navController)
        }

        composable(
            weatherForecastScreenRoute, arguments = listOf(
                navArgument("latitude") { type = NavType.StringType },
                navArgument("longitude") { type = NavType.StringType },
                navArgument("altitude") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude") ?: "0.0"
            val longitude = backStackEntry.arguments?.getString("longitude") ?: "0.0"
            val altitude = backStackEntry.arguments?.getString("altitude") ?: "0.0"

            WeatherForecastScreen(
                navController, lat = latitude, lon = longitude, altitude = altitude
            )
        }

        composable(favoriteScreenRoute) {
            FavoriteScreen(navController)
        }

        composable(contactScreenRoute) {
            ContactScreen(navController)
        }
    }
}
