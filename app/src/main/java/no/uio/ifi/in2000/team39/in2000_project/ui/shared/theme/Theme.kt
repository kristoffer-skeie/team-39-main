package no.uio.ifi.in2000.team39.in2000_project.ui.shared.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

/**
 * Contains theme settings for the IN2000 project, defining both dark and light color schemes.
 * It handles dynamic theming based on system settings or user preferences stored in a DataStore.
 */

private val DarkColorScheme = darkColorScheme(
    surface = VeryDarkGray,
    onSurface = White,
    onSurfaceVariant = MediumGrey.copy(0.75F),
    primaryContainer = DarkGray,
    onPrimaryContainer = White,
    secondary = MediumGrey,
    onSecondary = Black,
    tertiary = DarkMainButtonColor,
    onTertiary = DarkOnMainButtonColor,
)

private val LightColorScheme = lightColorScheme(
    surface = Navy,
    onSurface = Black,
    onSurfaceVariant = Black.copy(0.85F),
    primaryContainer = IceBlue,
    onPrimaryContainer = Black,
    secondary = Blue,
    onSecondary = Black,
    tertiary = LightMainButtonColor,
    onTertiary = LightOnMainButtonColor,
)

/**
 * Extension property for the [Context] class, providing a reference to the DataStore instance used to store user preferences, named "settings_preferences".
 */
val Context.dataStore by preferencesDataStore(name = "settings_preferences")

/**
 * Key used in the DataStore to persist the user's dark theme preference, allowing the application to remember this setting across sessions.
 */
val IS_DARK_THEME_ENABLED_KEY = booleanPreferencesKey("is_dark_theme_enabled")

/**
 * Applies the appropriate theme based on the user's preferences and system settings, supporting
 * dynamic theming on devices running Android Version S or higher. The [darkTheme] parameter indicates
 * if the dark theme should be used, defaulting to system settings. The [dynamicColor] parameter,
 * when true, utilizes dynamic color theming if supported by the OS. The [content] parameter represents
 * the composable content that will be themed according to the selected theme.
 */
@Composable
fun In2000_projectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val isDarkThemeEnabled by context.dataStore.data.map { preferences ->
        preferences[IS_DARK_THEME_ENABLED_KEY] ?: false
    }.collectAsState(initial = darkTheme)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDarkThemeEnabled) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
        }

        isDarkThemeEnabled -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            (context as? Activity)?.let { activity ->
                val window = activity.window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !isDarkThemeEnabled
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}

