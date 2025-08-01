package com.softklass.linkbarn.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.R
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.ui.partials.DismissBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val allCategories by viewModel.allCategories.collectAsState(initial = emptyList())
    val pendingDeletions by viewModel.pendingDeletions.collectAsState()
    val snackbarState by viewModel.snackbarState.collectAsState()

    // Filter out categories that are pending deletion
    val categories = allCategories.filter { category -> category.id !in pendingDeletions }
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }

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

    // Process pending deletions when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.processPendingDeletions()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add Category",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (categories.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_empty_state),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "No categories yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                        Text(
                            text = "Create your first category to organize your links",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 32.dp),
                        )
                    }
                }
            } else {
                // Categories list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(categories, key = { it.id }) { category ->
                        CategoryItem(
                            category = category,
                            onDelete = { viewModel.deleteCategory(category) },
                            onEdit = {
                                categoryToEdit = category
                                showEditCategoryDialog = true
                            },
                        )
                    }
                }
            }
        }
    }

    // Add category dialog
    CategoryDialog(
        viewModel = viewModel,
        showDialog = showAddCategoryDialog,
        onDismiss = { showAddCategoryDialog = false },
        categoryToEdit = null,
    )

    // Edit category dialog
    CategoryDialog(
        viewModel = viewModel,
        showDialog = showEditCategoryDialog,
        onDismiss = {
            showEditCategoryDialog = false
            categoryToEdit = null
        },
        categoryToEdit = categoryToEdit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false // Don't confirm the dismissal, just trigger the action
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // Don't confirm the dismissal, just trigger the action
                }
                else -> false
            }
        },
        positionalThreshold = { distance -> distance * 0.25f },
    )

    // Reset the dismiss state after the action is triggered
    LaunchedEffect(dismissState.currentValue, category.id) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            // Wait for the swipe animation to complete
            kotlinx.coroutines.delay(100)
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = { DismissBackground() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Category icon (rounded)
                Card(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = category.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                // Category name
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun CategoryDialog(
    viewModel: CategoriesViewModel,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    categoryToEdit: Category? = null,
) {
    val categoryUiState by viewModel.categoryUiState.collectAsState()
    var categoryName by rememberSaveable { mutableStateOf(categoryToEdit?.name ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isEditing = categoryToEdit != null

    // Update categoryName when categoryToEdit changes
    LaunchedEffect(categoryToEdit) {
        categoryName = categoryToEdit?.name ?: ""
    }

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
        AlertDialog(
            onDismissRequest = {
                onDismiss()
                viewModel.resetCategoryState()
            },
            title = { Text(if (isEditing) "Edit Category" else "Create Category") },
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
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Sentences),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isEditing) {
                                    viewModel.updateCategory(categoryToEdit, categoryName)
                                } else {
                                    viewModel.addCategory(categoryName)
                                }
                            },
                        ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateCategory(categoryToEdit, categoryName)
                        } else {
                            viewModel.addCategory(categoryName)
                        }
                    },
                    enabled = categoryUiState !is CategoryUiState.Loading,
                ) {
                    if (categoryUiState is CategoryUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(if (isEditing) "Update" else "Create")
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
