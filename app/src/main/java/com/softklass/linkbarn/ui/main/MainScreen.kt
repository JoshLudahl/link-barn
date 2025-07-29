package com.softklass.linkbarn.ui.main

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.R
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    // Snackbar state handling
    val snackbarState by viewModel.snackbarState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle snackbar visibility
    LaunchedEffect(snackbarState) {
        val currentState = snackbarState
        when (currentState) {
            is SnackbarState.Visible -> {
                val result = snackbarHostState.showSnackbar(
                    message = currentState.message,
                    actionLabel = "Undo",
                    withDismissAction = true,
                )
                when (result) {
                    androidx.compose.material3.SnackbarResult.ActionPerformed -> {
                        viewModel.undoDelete()
                    }
                    androidx.compose.material3.SnackbarResult.Dismissed -> {
                        viewModel.hideSnackbar()
                    }
                }
            }
            is SnackbarState.Hidden -> {
                // Do nothing
            }
        }
    }

    // Add the bottom sheet
    ModalBottomSheetAddUrl(
        openBottomSheet = openBottomSheet,
        onOpenBottomSheetChange = { openBottomSheet = it },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            BottomAppBar(
                // modifier = Modifier.height(56.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                windowInsets =
                WindowInsets(
                    left = 8.dp,
                    top = 0.dp,
                    right = 0.dp,
                    bottom = 16.dp,
                ),
                actions = {

                    IconButton(onClick = { onNavigateToCategories() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_category),
                            contentDescription = "Categories",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    IconButton(onClick = { onNavigateToSettings() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            // modifier = Modifier.size(24.dp),
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { openBottomSheet = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add Link",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(padding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(intrinsicSize = IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f),
                ) {
                    Text(
                        fontSize = 22.sp,
                        text = stringResource(id = R.string.main_screen_title),
                    )
                    Text(text = "Your saved links.")
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
            )

            // Filter segmented button
            val currentFilter by viewModel.currentFilter.collectAsState()
            val allLinks by viewModel.allLinks.collectAsState()

            // Only show filter buttons if there are any links
            if (allLinks.isNotEmpty()) {
                Column {
                    Text(
                        text = "Filter by viewed".uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Use SingleChoiceSegmentedButtonRow for filter buttons
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            // All button
                            SegmentedButton(
                                selected = currentFilter == LinkFilter.ALL,
                                onClick = { viewModel.setFilter(LinkFilter.ALL) },
                                modifier = Modifier.weight(1f),
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 0,
                                    count = 3,
                                ),
                                label = {
                                    Text("All")
                                },
                            )

                            // Unviewed button
                            SegmentedButton(
                                selected = currentFilter == LinkFilter.UNVISITED,
                                onClick = { viewModel.setFilter(LinkFilter.UNVISITED) },
                                modifier = Modifier.weight(1f),
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 1,
                                    count = 3,
                                ),
                                label = {
                                    Text("Unviewed")
                                },
                            )

                            // Viewed button
                            SegmentedButton(
                                selected = currentFilter == LinkFilter.VISITED,
                                onClick = { viewModel.setFilter(LinkFilter.VISITED) },
                                modifier = Modifier.weight(1f),
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = 2,
                                    count = 3,
                                ),
                                label = {
                                    Text("Viewed")
                                },
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )

            // Category filter chips
            val allCategories by viewModel.allCategories.collectAsState()
            val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()

            if (allCategories.isNotEmpty()) {
                Text(
                    text = "Filter by category".uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // "All" chip
                    item {
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategoryIds.isEmpty(),
                            onClick = { viewModel.selectCategoryFilter(null) },
                            label = { Text("All") },
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }

                    // Category chips
                    items(allCategories) { category ->
                        androidx.compose.material3.FilterChip(
                            selected = selectedCategoryIds.contains(category.id),
                            onClick = { viewModel.selectCategoryFilter(category.id) },
                            label = { Text(category.name) },
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
            }

            Row {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    val links by viewModel.links.collectAsState()
                    val deletingLinkIds by viewModel.deletingLinkIds.collectAsState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (links.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    // Customize empty state text based on current filter
                                    val emptyStateText = when (currentFilter) {
                                        LinkFilter.VISITED -> "No viewed links"
                                        LinkFilter.UNVISITED -> "No unviewed links"
                                        LinkFilter.ALL -> "No links added yet"
                                        LinkFilter.CATEGORY -> "No links in this category"
                                    }

                                    Text(
                                        text = emptyStateText,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )

                                    // Only show the full empty state UI for the ALL filter
                                    if (currentFilter == LinkFilter.ALL) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Icon(
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(16.dp),
                                            painter = painterResource(id = R.drawable.ic_empty_state),
                                            contentDescription = "No links",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { openBottomSheet = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        ) {
                                            Icon(
                                                painterResource(R.drawable.ic_add),
                                                contentDescription = "Add Link",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(20.dp),
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Add Link",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(
                                items = links,
                                key = { link -> link.id },
                            ) { link ->
                                AnimatedVisibility(
                                    visible = !deletingLinkIds.contains(link.id),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { -it },
                                        animationSpec = tween(300),
                                    ),
                                ) {
                                    LinkItem(link = link, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LinkItem(link: Link, viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    rememberCoroutineScope()

    // State to track if the item is in edit mode
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editName by rememberSaveable { mutableStateOf(link.name ?: "") }
    var editUrl by rememberSaveable { mutableStateOf(link.uri.toString()) }

    // Observe edit state
    val editLinkUiState by viewModel.editLinkUiState.collectAsState()

    // Effect to handle edit state changes
    LaunchedEffect(editLinkUiState) {
        when (editLinkUiState) {
            is EditLinkUiState.Success -> {
                isEditing = false
                viewModel.resetEditState()
            }
            else -> { /* No action needed */ }
        }
    }

    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                viewModel.deleteLink(link)
                false // Don't confirm the dismissal, just trigger the action
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.01f },
    )

    // Reset the dismiss state after the action is triggered
    LaunchedEffect(dismissState.currentValue, link.id) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            // Wait for the swipe animation to complete
            kotlinx.coroutines.delay(100)
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp, 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        },
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable(enabled = !isEditing) {
                    // Mark the link as visited
                    viewModel.markLinkAsVisited(link)
                    // Open the link in browser
                    val intent = Intent(Intent.ACTION_VIEW, link.uri.toString().lowercase().toUri())
                    context.startActivity(intent)
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            if (isEditing) {
                // Edit mode UI
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    // Error message
                    var localErrorMessage by remember { mutableStateOf<String?>(null) }

                    if (editLinkUiState is EditLinkUiState.Error) {
                        Text(
                            text = (editLinkUiState as EditLinkUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontSize = 12.sp,
                        )
                    } else if (localErrorMessage != null) {
                        Text(
                            text = localErrorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontSize = 12.sp,
                        )
                    }

                    // Name field
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // URL field
                    OutlinedTextField(
                        value = editUrl,
                        onValueChange = { editUrl = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categories field
                    val categories = remember(link.id) { mutableStateOf<List<Category>>(emptyList()) }
                    var showEditCategoryDialog by remember { mutableStateOf(false) }

                    LaunchedEffect(link.categoryIds) {
                        categories.value = viewModel.getCategoriesForLink(link)
                        // Initialize selected categories with the link's current categories
                        viewModel.clearSelectedCategories()
                        categories.value.forEach { category ->
                            viewModel.selectCategory(category)
                        }
                    }

                    // Show category creation dialog
                    CategoryDialog(
                        viewModel = viewModel,
                        showDialog = showEditCategoryDialog,
                        onDismiss = { showEditCategoryDialog = false },
                    )

                    // Categories section
                    Text(
                        text = "Categories".uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                    )

                    // Show all categories as filter chips
                    val allCategories by viewModel.allCategories.collectAsState()
                    val selectedCategories by viewModel.selectedCategories.collectAsState()

                    if (allCategories.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(allCategories) { category ->
                                val isSelected = selectedCategories.contains(category)
                                androidx.compose.material3.FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            viewModel.unselectCategory(category)
                                        } else {
                                            viewModel.selectCategory(category)
                                        }
                                    },
                                    label = { Text(category.name) },
                                )
                            }
                        }
                    }

                    // Button to create a new category
                    Button(
                        onClick = { showEditCategoryDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                    ) {
                        Text("Create New Category")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                isEditing = false
                                editName = link.name ?: ""
                                editUrl = link.uri.toString()
                                viewModel.resetEditState()
                            },
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (selectedCategories.isEmpty()) {
                                    // Show error if no categories selected
                                    localErrorMessage = "Please select at least one category"
                                } else {
                                    localErrorMessage = null
                                    val categoryNames = selectedCategories.map { it.name }
                                    viewModel.editLink(link, editName, editUrl, categoryNames)
                                }
                            },
                            enabled = editLinkUiState !is EditLinkUiState.Loading,
                        ) {
                            if (editLinkUiState is EditLinkUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    }
                }
            } else {
                // View mode UI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = link.name ?: "Untitled",
                                fontSize = 18.sp,
                                fontWeight = if (link.visited) FontWeight.Normal else FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (link.visited) "Viewed" else "Unviewed",
                                fontSize = 10.sp,
                                color = if (link.visited) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.tertiary
                                },
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = link.uri.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            // Display categories if any
                            if (link.categoryIds.isNotEmpty()) {
                                val categories = remember(link.categoryIds) {
                                    mutableStateOf<List<Category>>(emptyList())
                                }

                                // Load categories for this link
                                LaunchedEffect(link.categoryIds) {
                                    categories.value = viewModel.getCategoriesForLink(link)
                                }

                                // Use a Row to display category chips
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp),
                                ) {
                                    categories.value.take(3).forEach { category ->

                                        ElevatedAssistChip(
                                            onClick = { },
                                            label = {
                                                Text(
                                                    text = category.name,
                                                    fontSize = 10.sp,
                                                )
                                            },
                                            modifier = Modifier.height(24.dp),
                                        )
                                    }

                                    // Show count of additional categories if there are more than 3
                                    if (categories.value.size > 3) {
                                        Text(
                                            text = "+${categories.value.size - 3} more",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(start = 4.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, link.uri.toString())
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                },

                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),

                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_share),
                                    contentDescription = "Share link",
                                    modifier = Modifier
                                        .size(24.dp),
                                )
                            }

                            IconButton(
                                onClick = { isEditing = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_edit_note),
                                    contentDescription = "Edit link",
                                    modifier = Modifier
                                        .size(24.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    viewModel: MainViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit,
) {
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    var categoryName by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(categoryUiState) {
        when (categoryUiState) {
            is CategoryUiState.Error -> {
                errorMessage = (categoryUiState as CategoryUiState.Error).message
            }
            is CategoryUiState.Success -> {
                errorMessage = null
                categoryName = ""
                onDismiss()
                viewModel.resetCategoryState()
            }
            else -> {
                errorMessage = null
            }
        }
    }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                onDismiss()
                viewModel.resetCategoryState()
            },
            title = { Text("Create Category") },
            text = {
                Column {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage != null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.addCategory(categoryName)
                            },
                        ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addCategory(categoryName)
                    },
                    enabled = categoryUiState !is CategoryUiState.Loading,
                ) {
                    if (categoryUiState is CategoryUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Create")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                        viewModel.resetCategoryState()
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetAddUrl(
    viewModel: MainViewModel = hiltViewModel(),
    openBottomSheet: Boolean = false,
    onOpenBottomSheetChange: (Boolean) -> Unit = {},
) {
    var url by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val nameFocusRequester = remember { FocusRequester() }

    val uiState by viewModel.uiState.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()

    // Show category creation dialog
    CategoryDialog(
        viewModel = viewModel,
        showDialog = showCategoryDialog,
        onDismiss = { showCategoryDialog = false },
    )

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddLinkUiState.Error -> {
                errorMessage = (uiState as AddLinkUiState.Error).message
            }

            is AddLinkUiState.Success -> {
                errorMessage = null
                url = ""
                name = ""
                viewModel.clearSelectedCategories()
                scope.launch {
                    bottomSheetState.hide()
                    onOpenBottomSheetChange(false)
                }
                viewModel.resetState()
            }

            else -> {
                errorMessage = null
            }
        }
    }

    // Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onOpenBottomSheetChange(false)
                errorMessage = null
                viewModel.resetState()
                viewModel.clearSelectedCategories()
            },
            sheetState = bottomSheetState,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Cancel button
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                onOpenBottomSheetChange(false)
                                errorMessage = null
                                viewModel.resetState()
                                viewModel.clearSelectedCategories()
                            }
                        },
                    ) {
                        Text(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            text = "Cancel",
                        )
                    }
                }

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                // URL Input Field
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            nameFocusRequester.requestFocus()
                        },
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Input Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nameFocusRequester),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // No action needed here
                        },
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categories Section

                Spacer(modifier = Modifier.height(8.dp))

                // Category Chip Group
                if (allCategories.isNotEmpty()) {
                    Text(
                        text = "Categories".uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(allCategories) { category ->
                            val isSelected = selectedCategories.contains(category)
                            androidx.compose.material3.FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        viewModel.unselectCategory(category)
                                    } else {
                                        viewModel.selectCategory(category)
                                    }
                                },
                                label = { Text(category.name) },
                            )
                        }
                    }
                }

                // Button to create a new category
                Button(
                    onClick = { showCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text("Create New Category")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        if (selectedCategories.isEmpty()) {
                            errorMessage = "Please select at least one category"
                        } else {
                            val categoryNames = selectedCategories.map { it.name }
                            viewModel.addLink(name, url, categoryNames)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is AddLinkUiState.Loading,
                ) {
                    if (uiState is AddLinkUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        }
    }
}
