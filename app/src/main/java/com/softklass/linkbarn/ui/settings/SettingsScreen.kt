package com.softklass.linkbarn.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.ui.theme.ThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val dynamicColorEnabled by settingsViewModel.dynamicColorEnabled.collectAsState(initial = true)
    val themeMode by settingsViewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()

    // State to prevent double-clicking the back button
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isBackButtonEnabled) {
                                // Disable the button to prevent double-clicking
                                isBackButtonEnabled = false

                                // Perform the navigation
                                onNavigateBack()

                                // Re-enable the button after a delay (if needed)
                                scope.launch {
                                    kotlinx.coroutines.delay(500) // 500ms delay
                                    isBackButtonEnabled = true
                                }
                            }
                        },
                        enabled = isBackButtonEnabled,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            Text(
                text = "Appearance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Dynamic Color",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Use colors from your wallpaper to personalize your app experience",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SegmentedButton(
                        selected = !dynamicColorEnabled,
                        onClick = {
                            scope.launch {
                                settingsViewModel.setDynamicColorEnabled(false)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    ) {
                        Text("Theme")
                    }

                    SegmentedButton(
                        selected = dynamicColorEnabled,
                        onClick = {
                            scope.launch {
                                settingsViewModel.setDynamicColorEnabled(true)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    ) {
                        Text("Dynamic")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Theme Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "Choose between light, dark, or system theme",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SegmentedButton(
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = {
                            scope.launch {
                                settingsViewModel.setThemeMode(ThemeMode.LIGHT)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                    ) {
                        Text("Light")
                    }

                    SegmentedButton(
                        selected = themeMode == ThemeMode.DARK,
                        onClick = {
                            scope.launch {
                                settingsViewModel.setThemeMode(ThemeMode.DARK)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    ) {
                        Text("Dark")
                    }

                    SegmentedButton(
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            scope.launch {
                                settingsViewModel.setThemeMode(ThemeMode.SYSTEM)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    ) {
                        Text("System")
                    }
                }
            }

            // Push the version to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Display app version at the bottom
            Text(
                text = "Version: ${settingsViewModel.appVersion}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
