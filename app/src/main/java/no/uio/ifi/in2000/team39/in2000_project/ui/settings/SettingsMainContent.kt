package no.uio.ifi.in2000.team39.in2000_project.ui.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.in2000_project.R

/**
 * Renders the settings interface for the application, providing controls for theme adjustments.
 * The function encapsulates UI components that allow the user to toggle the dark mode theme on or off.
 * The [paddingValues] parameter provides padding applied externally to this component.
 * The [isDarkThemeEnabled] parameter indicates whether the dark theme is currently enabled.
 * The [onToggleDarkTheme] parameter is a callback triggered when the user toggles the dark theme switch.
 */
@Composable
fun SettingsMainContent(
    paddingValues: PaddingValues,
    isDarkThemeEnabled: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(25.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(id = R.string.setting_sub_title),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .semantics { heading() }
        )

        HorizontalDivider(
            thickness = 1.dp, color = Color.LightGray
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleDarkTheme(!isDarkThemeEnabled) }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkModeToggle(
                isDarkThemeEnabled = isDarkThemeEnabled,
                onToggleDarkTheme = onToggleDarkTheme
            )
        }
    }
}

/**
 * Displays a row for toggling the dark mode setting, including a label, an icon that changes based on the current theme, and a switch.
 * The [isDarkThemeEnabled] parameter indicates whether the dark theme is currently enabled.
 * The [onToggleDarkTheme] parameter is a callback triggered when the user toggles the dark theme switch.
 */
@Composable
fun DarkModeToggle(
    isDarkThemeEnabled: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleDarkTheme(!isDarkThemeEnabled) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(id = R.string.dark_mode_toggle_text))

        Crossfade(
            targetState = isDarkThemeEnabled,
            label = ""
        ) { isDark ->
            Icon(
                imageVector = if (isDark) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                contentDescription = stringResource(id = R.string.dark_mode_toggle_description)
            )
        }

        Switch(
            checked = isDarkThemeEnabled,
            onCheckedChange = onToggleDarkTheme,
            modifier = Modifier.defaultMinSize(48.dp)
        )
    }
}