package com.softklass.linkbarn.ui.dashboard

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    val clickedLinks by viewModel.clickedLinks.collectAsState()

    // Define grid parameters
    val numberOfColumns = 2
    val totalGridCells = 18

    // Prepare items for the grid, including placeholders
    val gridItems = remember(clickedLinks) {
        val actualItems = clickedLinks.take(totalGridCells) // Take at most 18 items
        val placeholderCount = totalGridCells - actualItems.size
        actualItems + List(placeholderCount) { PlaceholderItem } // Add placeholders
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isBackButtonEnabled) {
                                isBackButtonEnabled = false
                                onNavigateBack()

                                scope.launch {
                                    delay(500) // 500ms delay
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
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (clickedLinks.isEmpty()) {
                EmptyDashboard()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(numberOfColumns), // Fixed number of columns
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize(), // Allow grid to take up available space
                ) {
                    items(gridItems.size) { index ->
                        // Iterate up to totalGridCells
                        val item = gridItems[index]
                        if (item is Link) {
                            ClickedLinkCard(
                                link = item,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, item.uri.toString().lowercase().toUri())
                                    context.startActivity(intent)
                                },
                            )
                        } else {
                            PlaceholderCard() // Your Composable for placeholder items
                        }
                    }
                }
            }
        }
    }
}

// Define a simple data object for placeholders (or use null or a specific class)
object PlaceholderItem

@Composable
fun PlaceholderCard() {
    Card(
        modifier = Modifier
            .fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f), // Dim color for placeholder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Optional: no elevation
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize() // Make placeholder take the same space as a real card
                .padding(12.dp), // Match padding of ClickedLinkCard
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\n", // Reserves space for two lines, similar to title and host
                style = MaterialTheme.typography.titleSmall, // Use similar typography
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f), // Invisible
            )
            Text(
                text = " ", // Reserves space for one line
                style = MaterialTheme.typography.bodySmall, // Use similar typography
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0f), // Invisible
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun ClickedLinkCard(
    link: Link,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = link.name?.takeIf { it.isNotBlank() } ?: "Unnamed Link",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = link.uri.host ?: link.uri.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun EmptyDashboard() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "No links clicked yet",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Click on links from the main screen to see them here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
