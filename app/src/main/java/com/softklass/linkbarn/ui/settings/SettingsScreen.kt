package com.softklass.linkbarn.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.color.DynamicColors
import com.softklass.linkbarn.ui.partials.TopAppBarIcon
import com.softklass.linkbarn.ui.theme.ThemeMode
import com.softklass.linkbarn.utils.shareAppIntent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
    val context = LocalContext.current

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
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    TopAppBarIcon { shareAppIntent(context) }
                },
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

            if (DynamicColors.isDynamicColorAvailable()) {
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

                    val options = listOf("Default", "Dynamic")

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ToggleButtonDefaults.IconSpacing),
                    ) {
                        options.forEachIndexed { index, label ->

                            ToggleButton(
                                checked = if (dynamicColorEnabled && label == "Dynamic") {
                                    true
                                } else if (!dynamicColorEnabled && label == "Default") {
                                    true
                                } else {
                                    false
                                },
                                onCheckedChange = {
                                    scope.launch {
                                        settingsViewModel.setDynamicColorEnabled(!dynamicColorEnabled)
                                    }
                                },
                                modifier = Modifier.weight(1f),

                                shapes =
                                when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                },
                            ) {
                                if (dynamicColorEnabled && label == "Dynamic" || !dynamicColorEnabled && label == "Default") {
                                    Icon(
                                        Icons.Rounded.Done,
                                        contentDescription = "Localized description",
                                    )
                                }

                                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))

                                Text(label, maxLines = 1)
                            }
                        }
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

                val options = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)

                // ButtonGroup for Material 3 Expressive

                Row(
                    horizontalArrangement = Arrangement.spacedBy(ToggleButtonDefaults.IconSpacing),
                ) {
                    options.forEachIndexed { index, label ->
                        ToggleButton(
                            checked = themeMode == label,
                            onCheckedChange = {
                                scope.launch {
                                    settingsViewModel.setThemeMode(label)
                                }
                            },
                            modifier = Modifier.weight(1f),

                            shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                        ) {
                            if (themeMode == label) {
                                Icon(
                                    Icons.Rounded.Done,
                                    contentDescription = "Localized description",
                                )
                            }

                            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))

                            Text(label.name.lowercase().replaceFirstChar { it.titlecaseChar() }, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Barn Owl",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The Link Barn is inspired after the Barn Owl. It is a symbol of wisdom and knowledge.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            AttributionAnnotatedText()

            // Push the version and review button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Add a section for app feedback
            Text(
                text = "Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Get the current context
            val context = LocalContext.current

            Button(
                onClick = { settingsViewModel.openPlayStoreForReview(context) },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = "Leave a review icon",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Rate this app")
            }

            Spacer(modifier = Modifier.height(16.dp))

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

@Composable
fun AttributionAnnotatedText() {
    val annotatedLinkString: AnnotatedString = remember {
        buildAnnotatedString {
            val style = SpanStyle()

            val styleCenter = SpanStyle(
                color = Color(0xff64B5F6),
                textDecoration = TextDecoration.Underline,
            )

            withStyle(
                style = style,
            ) {
                append("To learn more, ")
            }

            withLink(LinkAnnotation.Url(url = "https://myodfw.com/wildlife-viewing/species/barn-owl")) {
                withStyle(
                    style = styleCenter,
                ) {
                    append("click here")
                }
            }

            withStyle(
                style = style,
            ) {
                append(".")
            }
        }
    }

    Column {
        Text(
            text = annotatedLinkString,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
