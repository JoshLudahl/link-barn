package com.softklass.linkbarn.ui.categories

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.ui.partials.SwipeToDismissContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val showAddCategoryDialog = remember { mutableStateOf(false) }
    val showEditCategoryDialog = remember { mutableStateOf(false) }
    val categoryToEdit = remember { mutableStateOf<Category?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text("Categories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (categories.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showAddCategoryDialog.value = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add Category",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            } else {
                ExtendedFloatingActionButton(
                    onClick = { showAddCategoryDialog.value = true },
                    icon = { Icon(Icons.Rounded.Add, "Add a category", tint = MaterialTheme.colorScheme.onPrimary) },
                    text = { Text(text = "Add a category") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                )
            }
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (categories.isEmpty()) {
                EmptyState()
            } else {
                ShowList(
                    categories = categories,
                    onSwipeRightToLeft = {
                        viewModel.deleteCategory(it)

                        scope.launch {
                            val result = snackbarHostState
                                .showSnackbar(
                                    message = "Category ${it.name} deleted",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true,
                                )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    Log.d("CategoriesScreen", "Undo delete tapped ${it.name}")
                                    viewModel.undoDelete()
                                }

                                SnackbarResult.Dismissed -> {
                                    Log.d("CategoriesScreen", "Snackbar dismissed")
                                }
                            }
                        }
                    },
                    onSwipeLeftToRight = {
                        categoryToEdit.value = it
                        showEditCategoryDialog.value = true
                    },
                )
            }
        }
    }

    // Add category dialog
    CategoryDialog(
        viewModel = viewModel,
        showDialog = showAddCategoryDialog.value,
        onDismiss = { showAddCategoryDialog.value = false },
        categoryToEdit = null,
    )

    // Edit category dialog
    CategoryDialog(
        viewModel = viewModel,
        showDialog = showEditCategoryDialog.value,
        onDismiss = {
            showEditCategoryDialog.value = false
            categoryToEdit.value = null
        },
        categoryToEdit = categoryToEdit.value,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryItem(
    category: Category,
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
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Sentences,
                        ),
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

@Composable
fun ShowList(
    categories: List<Category> = emptyList(),
    onSwipeRightToLeft: (Category) -> Unit,
    onSwipeLeftToRight: (Category) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            SwipeToDismissContainer(
                item = category.name,
                onSwipeRightToLeft = { onSwipeRightToLeft(category) },
                onSwipeLeftToRight = { onSwipeLeftToRight(category) },
            ) {
                CategoryItem(
                    category = category,
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Category,
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
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 32.dp),
            )
        }
    }
}
