package no.uio.ifi.in2000.team39.in2000_project.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar

/**
 * Constructs the settings screen of the application, providing user interface components for interacting
 * with application settings such as theme toggling. This screen includes a top bar with navigation controls,
 * a bottom navigation bar, and the main content area dedicated to settings configurations. The [navController]
 * parameter manages navigation and stack manipulation in the application, allowing for back navigation,
 * while the [settingsViewModel] parameter holds the data and functions related to settings, including the theme state.
 */
@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val isDarkThemeEnabled by settingsViewModel.isDarkThemeEnabled.collectAsState(initial = true)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            TopBar(
                onBackClick = { navController.popBackStack() },
                title = stringResource(id = R.string.setting_screen_title)
            )
        },
        bottomBar = {
            BottomBar(
                navController,
            )
        }
    ) { paddingValues ->
        SettingsMainContent(
            paddingValues = paddingValues,
            isDarkThemeEnabled = isDarkThemeEnabled,
            onToggleDarkTheme = settingsViewModel::toggleDarkTheme
        )
    }
}
