package no.uio.ifi.in2000.team39.in2000_project.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar

/**
 * Displays the home screen of the app with a top bar, bottom navigation bar, and the main content area.
 * The main content area includes a button that navigates to the map screen when clicked. It makes use of
 * the [NavController] to handle screen transitions.
 */
@Composable
fun HomeScreen(
    navController: NavController,
) {
    val settingsRoute = stringResource(id = R.string.setting_screen_route)
    val mapRoute = stringResource(id = R.string.map_screen_route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopBar(
                onSettingsClick = { navController.navigate(settingsRoute) },
                title = stringResource(id = R.string.home_screen_title)
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->
        HomeMainContent(
            onButtonClick = { navController.navigate(mapRoute) },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
