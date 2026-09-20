package no.uio.ifi.in2000.team39.in2000_project.ui.settings

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.theme.dataStore

/**
 * ViewModel responsible for managing the settings state, particularly the theme setting for the application.
 * It utilizes DataStore for persisting user preferences across sessions, ensuring that the dark theme preference
 * persists even after the application restarts.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    private companion object {
        val IS_DARK_THEME_ENABLED_KEY = booleanPreferencesKey("is_dark_theme_enabled")
    }

    /**
     * Returns a flow of the current dark theme enabled state. This state is observed in the UI
     * to reactively update the theme of the app based on user preferences.
     */
    val isDarkThemeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_THEME_ENABLED_KEY] ?: false
    }

    /**
     * Toggles the dark theme setting by updating the value in DataStore.
     * This function is called when the user interacts with the theme toggle in the settings UI. The new
     * state of the dark theme setting depends on [isEnabled], true if dark theme should be enabled, false otherwise.
     */
    fun toggleDarkTheme(isEnabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { settings ->
                settings[IS_DARK_THEME_ENABLED_KEY] = isEnabled
            }
        }
    }
}
