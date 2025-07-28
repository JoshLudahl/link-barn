package com.softklass.linkbarn.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
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

    // Open the Play Store to leave a review
    fun openPlayStoreForReview(context: Context) {
        val packageName = context.packageName
        val uri = "market://details?id=$packageName".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)

        // Add flags to start a new task
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not available, open in browser
            Log.e("SettingsViewModel", "Error opening Play Store: ${e.message}")
            val webUri = "https://play.google.com/store/apps/details?id=$packageName".toUri()
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }
}
