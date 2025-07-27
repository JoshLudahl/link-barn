package com.softklass.linkbarn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.softklass.linkbarn.data.preferences.SettingsPreferences
import com.softklass.linkbarn.navigation.AppNavHost
import com.softklass.linkbarn.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(settingsPreferences = settingsPreferences) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
