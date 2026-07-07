package com.softklass.linkbarn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.softklass.linkbarn.data.preferences.SettingsPreferences
import com.softklass.linkbarn.navigation.AppNavHost
import com.softklass.linkbarn.ui.theme.AppTheme
import com.softklass.linkbarn.utils.InAppUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    private lateinit var inAppUpdateManager: InAppUpdateManager

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.i("MainActivity", "The Update has failed.")
            }
        }

    companion object {
        var sharedUrl: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Handle shared URL intent
        handleIntent(intent)

        inAppUpdateManager = InAppUpdateManager(this, activityResultLauncher)
        inAppUpdateManager.checkForUpdate()

        setContent {
            AppTheme(settingsPreferences = settingsPreferences) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Scaffold(
                        modifier = Modifier.systemBarsPadding(),
                    ) { padding ->
                        AppNavHost(
                            modifier = Modifier.padding(padding),
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                sharedUrl = sharedText
                Log.d("MainActivity", "Received shared URL: $sharedText")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdateManager.unregister()
    }
}
