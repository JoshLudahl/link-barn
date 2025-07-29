package com.softklass.linkbarn.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

    val allCategories = categoryRepository.getAllCategories()

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
        viewModelScope.launch(dispatcher) {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                // Handle error if needed - could add error state for delete operations
            }
        }
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
