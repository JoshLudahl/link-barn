package com.softklass.linkbarn.ui.settings

import androidx.lifecycle.ViewModel
import com.softklass.linkbarn.BuildConfig
import com.softklass.linkbarn.data.preferences.SettingsPreferences
import com.softklass.linkbarn.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
) : ViewModel() {

    // Expose the dynamic color preference as a Flow
    val dynamicColorEnabled = settingsPreferences.dynamicColorEnabled

    // Update the dynamic color preference
    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        settingsPreferences.setDynamicColorEnabled(enabled)
    }

    // Expose the theme mode preference as a Flow
    val themeMode = settingsPreferences.themeMode

    // Update the theme mode preference
    suspend fun setThemeMode(mode: ThemeMode) {
        settingsPreferences.setThemeMode(mode)
    }

    // Get the app version from BuildConfig
    val appVersion: String
        get() = BuildConfig.VERSION_NAME
}
