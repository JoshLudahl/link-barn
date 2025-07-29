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

    private var deletedCategory: Category? = null
    private var deleteJob: Job? = null

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
        // Cancel any existing delete job
        deleteJob?.cancel()

        // Store the deleted category for potential undo
        deletedCategory = category

        // Add category to pending deletions to hide it from UI
        _pendingDeletions.value = _pendingDeletions.value + category.id

        // Show snackbar with undo option
        _snackbarState.value = SnackbarState.Visible(
            message = "Category deleted",
            categoryName = category.name,
        )

        // Schedule permanent deletion after 5 seconds
        deleteJob = viewModelScope.launch(dispatcher) {
            try {
                delay(5000) // 5 seconds delay
                categoryRepository.deleteCategory(category)
                deletedCategory = null
                _pendingDeletions.value = _pendingDeletions.value - category.id
                _snackbarState.value = SnackbarState.Hidden
            } catch (e: Exception) {
                Log.e("CategoriesViewModel", "Failed to delete category: ${e.message}")
                // Handle error if needed - could add error state for delete operations
                _pendingDeletions.value = _pendingDeletions.value - category.id
                _snackbarState.value = SnackbarState.Hidden
            }
        }
    }

    fun undoDelete() {
        deleteJob?.cancel()
        deletedCategory?.let { category ->
            _pendingDeletions.value = _pendingDeletions.value - category.id
        }
        deletedCategory = null
        _snackbarState.value = SnackbarState.Hidden
    }

    fun hideSnackbar() {
        _snackbarState.value = SnackbarState.Hidden
    }

    fun resetCategoryState() {
        _categoryUiState.value = CategoryUiState.Initial
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
