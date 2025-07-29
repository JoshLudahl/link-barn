package com.softklass.linkbarn.ui.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Initial)
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState.asStateFlow()

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Hidden)
    val snackbarState: StateFlow<SnackbarState> = _snackbarState.asStateFlow()

    private val _pendingDeletions = MutableStateFlow<Set<String>>(emptySet())
    val pendingDeletions: StateFlow<Set<String>> = _pendingDeletions.asStateFlow()

    // Support multiple pending deletions
    private val deletedCategories = mutableMapOf<String, Category>()
    private val deleteJobs = mutableMapOf<String, Job>()

    private val _allCategories = categoryRepository.getAllCategories()
    val allCategories: Flow<List<Category>> = _allCategories

    fun addCategory(name: String) {
        if (name.isBlank()) {
            _categoryUiState.value = CategoryUiState.Error("Category name cannot be empty")
            return
        }

        viewModelScope.launch(dispatcher) {
            _categoryUiState.value = CategoryUiState.Loading
            try {
                val existingCategory = categoryRepository.getCategoryByName(name.trim())
                if (existingCategory != null) {
                    _categoryUiState.value = CategoryUiState.Error("Category already exists")
                    return@launch
                }

                val newCategory = Category(name = name.trim())
                categoryRepository.insertCategory(newCategory)
                _categoryUiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                _categoryUiState.value = CategoryUiState.Error("Failed to add category: ${e.message}")
            }
        }
    }

    fun updateCategory(category: Category, newName: String) {
        if (newName.isBlank()) {
            _categoryUiState.value = CategoryUiState.Error("Category name cannot be empty")
            return
        }

        viewModelScope.launch(dispatcher) {
            _categoryUiState.value = CategoryUiState.Loading
            try {
                val existingCategory = categoryRepository.getCategoryByName(newName.trim())
                if (existingCategory != null && existingCategory.id != category.id) {
                    _categoryUiState.value = CategoryUiState.Error("Category already exists")
                    return@launch
                }

                val updatedCategory = category.copy(name = newName.trim())
                categoryRepository.updateCategory(updatedCategory)
                _categoryUiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                _categoryUiState.value = CategoryUiState.Error("Failed to update category: ${e.message}")
            }
        }
    }

    fun deleteCategory(category: Category) {
        // Cancel any existing delete job for this category
        deleteJobs[category.id]?.cancel()

        // Store the deleted category for potential undo
        deletedCategories[category.id] = category

        // Add category to pending deletions to hide it from UI
        _pendingDeletions.value = _pendingDeletions.value + category.id

        // Show snackbar with undo option
        _snackbarState.value = SnackbarState.Visible(
            message = "Category deleted",
            categoryName = category.name,
        )

        // Schedule permanent deletion after 5 seconds
        deleteJobs[category.id] = viewModelScope.launch(dispatcher) {
            try {
                delay(5000) // 5 seconds delay
                categoryRepository.deleteCategory(category)
                deletedCategories.remove(category.id)
                deleteJobs.remove(category.id)
                _pendingDeletions.value = _pendingDeletions.value - category.id

                // Only hide snackbar if this is the last pending deletion
                if (_pendingDeletions.value.isEmpty()) {
                    _snackbarState.value = SnackbarState.Hidden
                }
            } catch (e: Exception) {
                // Only log if it's not a cancellation exception (which is expected during undo)
                if (e !is kotlinx.coroutines.CancellationException) {
                    Log.e("CategoriesViewModel", "Failed to delete category: ${e.message}")
                }
                // Handle error if needed - could add error state for delete operations
                deletedCategories.remove(category.id)
                deleteJobs.remove(category.id)
                _pendingDeletions.value = _pendingDeletions.value - category.id

                // Only hide snackbar if this is the last pending deletion
                if (_pendingDeletions.value.isEmpty()) {
                    _snackbarState.value = SnackbarState.Hidden
                }
            }
        }
    }

    fun undoDelete() {
        // Find the most recent deletion (the one shown in the snackbar)
        val currentSnackbar = _snackbarState.value
        if (currentSnackbar is SnackbarState.Visible) {
            // Find the category by name (since snackbar shows category name)
            val categoryToUndo = deletedCategories.values.find { it.name == currentSnackbar.categoryName }
            categoryToUndo?.let { category ->
                // Cancel the delete job for this category
                deleteJobs[category.id]?.cancel()
                deleteJobs.remove(category.id)

                // Remove from deleted categories and pending deletions
                deletedCategories.remove(category.id)
                _pendingDeletions.value = _pendingDeletions.value - category.id
            }
        }
        _snackbarState.value = SnackbarState.Hidden
    }

    fun hideSnackbar() {
        _snackbarState.value = SnackbarState.Hidden
    }

    fun resetCategoryState() {
        _categoryUiState.value = CategoryUiState.Initial
    }

    /**
     * Process all pending deletions immediately.
     * This should be called when the user leaves the screen.
     */
    fun processPendingDeletions() {
        viewModelScope.launch(dispatcher) {
            // Get a copy of pending deletions to avoid concurrent modification
            val pendingIds = _pendingDeletions.value.toSet()

            for (categoryId in pendingIds) {
                val category = deletedCategories[categoryId]
                if (category != null) {
                    try {
                        // Cancel the delayed deletion job
                        deleteJobs[categoryId]?.cancel()

                        // Delete immediately
                        categoryRepository.deleteCategory(category)

                        // Clean up
                        deletedCategories.remove(categoryId)
                        deleteJobs.remove(categoryId)
                        _pendingDeletions.value = _pendingDeletions.value - categoryId
                    } catch (e: Exception) {
                        // Only log if it's not a cancellation exception
                        if (e !is kotlinx.coroutines.CancellationException) {
                            Log.e("CategoriesViewModel", "Failed to process pending deletion for category ${category.name}: ${e.message}")
                        }
                        // Clean up even if deletion failed
                        deletedCategories.remove(categoryId)
                        deleteJobs.remove(categoryId)
                        _pendingDeletions.value = _pendingDeletions.value - categoryId
                    }
                }
            }

            // Hide snackbar after processing all deletions
            _snackbarState.value = SnackbarState.Hidden
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Process any remaining pending deletions when ViewModel is cleared
        processPendingDeletions()

        // Cancel all remaining jobs
        deleteJobs.values.forEach { it.cancel() }
        deleteJobs.clear()
        deletedCategories.clear()
    }
}

sealed class CategoryUiState {
    object Initial : CategoryUiState()
    object Loading : CategoryUiState()
    object Success : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

sealed class SnackbarState {
    object Hidden : SnackbarState()
    data class Visible(val message: String, val categoryName: String) : SnackbarState()
}
