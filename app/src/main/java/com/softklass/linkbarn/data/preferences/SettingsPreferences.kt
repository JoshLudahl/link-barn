package com.softklass.linkbarn.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.softklass.linkbarn.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore

    // Keys for preferences
    companion object {
        private val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        private val THEME_MODE = intPreferencesKey("theme_mode")
    }

    // Get the dynamic color preference as a Flow
    val dynamicColorEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        // Default to true if not set
        preferences[DYNAMIC_COLOR_ENABLED] ?: true
    }

    // Update the dynamic color preference
    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_ENABLED] = enabled
        }
    }

    // Get the theme mode preference as a Flow
    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        // Default to system theme if not set
        ThemeMode.fromValue(preferences[THEME_MODE] ?: ThemeMode.SYSTEM.value)
    }

    // Update the theme mode preference
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode.value
        }
    }

    // Get the dark theme preference as a Flow (for backward compatibility)
    val darkThemeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        // Convert theme mode to boolean (DARK = true, otherwise false)
        ThemeMode.fromValue(preferences[THEME_MODE] ?: ThemeMode.SYSTEM.value) == ThemeMode.DARK
    }

    // Update the dark theme preference (for backward compatibility)
    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            // Convert boolean to theme mode (true = DARK, false = LIGHT)
            preferences[THEME_MODE] = if (enabled) ThemeMode.DARK.value else ThemeMode.LIGHT.value
        }
    }
}
