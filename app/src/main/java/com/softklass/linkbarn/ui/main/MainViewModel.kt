package com.softklass.linkbarn.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import com.softklass.linkbarn.utils.UrlValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URI
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LinkFilter {
    ALL,
    VISITED,
    UNVISITED,
    CATEGORY,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val linkRepository: LinkDataRepository,
    private val categoryRepository: CategoryRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddLinkUiState>(AddLinkUiState.Initial)
    val uiState: StateFlow<AddLinkUiState> = _uiState

    private val _editLinkUiState = MutableStateFlow<EditLinkUiState>(EditLinkUiState.Initial)
    val editLinkUiState: StateFlow<EditLinkUiState> = _editLinkUiState

    private val _categoryUiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Initial)
    val categoryUiState: StateFlow<CategoryUiState> = _categoryUiState

    private val _currentFilter = MutableStateFlow(LinkFilter.ALL)
    val currentFilter: StateFlow<LinkFilter> = _currentFilter

    private val _selectedCategoryIds = MutableStateFlow<List<String>>(emptyList())
    val selectedCategoryIds: StateFlow<List<String>> = _selectedCategoryIds

    private val _selectedCategories = MutableStateFlow<List<Category>>(emptyList())
    val selectedCategories: StateFlow<List<Category>> = _selectedCategories

    private val _deletingLinkIds = MutableStateFlow<Set<String>>(emptySet())
    val deletingLinkIds: StateFlow<Set<String>> = _deletingLinkIds

    private val _snackbarState = MutableStateFlow<SnackbarState>(SnackbarState.Hidden)
    val snackbarState: StateFlow<SnackbarState> = _snackbarState.asStateFlow()

    private val _pendingDeletions = MutableStateFlow<Set<String>>(emptySet())
    val pendingDeletions: StateFlow<Set<String>> = _pendingDeletions.asStateFlow()

    private val _sharedUrl = MutableStateFlow<String?>(null)
    val sharedUrl: StateFlow<String?> = _sharedUrl.asStateFlow()

    private var pendingDeletingLink: Link? = null

    // Track all links separately to determine if we should show segmented buttons
    val allLinks: StateFlow<List<Link>> = linkRepository.getAllLinks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    // Get all categories
    val allCategories: StateFlow<List<Category>> = categoryRepository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val links: StateFlow<List<Link>> = kotlinx.coroutines.flow.combine(
        _currentFilter,
        _selectedCategoryIds,
        _pendingDeletions,
    ) { filter, selectedCategoryIds, pendingDeletions ->
        when (filter) {
            LinkFilter.ALL -> linkRepository.getAllLinks()
            LinkFilter.VISITED -> linkRepository.getVisitedLinks()
            LinkFilter.UNVISITED -> linkRepository.getUnvisitedLinks()
            LinkFilter.CATEGORY -> {
                if (selectedCategoryIds.isNotEmpty()) {
                    linkRepository.getLinksByCategories(selectedCategoryIds)
                } else {
                    // Return empty list when no categories are selected
                    kotlinx.coroutines.flow.flowOf(emptyList())
                }
            }
        }
    }.flatMapLatest { flow ->
        kotlinx.coroutines.flow.combine(flow, _pendingDeletions) { links, pendingDeletions ->
            // Filter out links that are pending deletion
            links.filter { link -> link.id !in pendingDeletions }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun addLink(name: String, url: String, categoryNames: List<String> = emptyList()) {
        viewModelScope.launch(dispatcher) {
            _uiState.value = AddLinkUiState.Loading

            try {
                if (name.isBlank()) {
                    _uiState.value = AddLinkUiState.Error("Name is required")
                    return@launch
                }

                if (url.isBlank()) {
                    _uiState.value = AddLinkUiState.Error("URL is required")
                    return@launch
                }

                if (!UrlValidator.isValid(url)) {
                    _uiState.value = AddLinkUiState.Error(
                        "Invalid URL format. URL must be a valid http:// or https:// address",
                    )
                    return@launch
                }

                val uri = URI(url)

                // Check if URL already exists
                val existingLink = linkRepository.getLinkByUri(uri)
                if (existingLink != null) {
                    _uiState.value = AddLinkUiState.Error("This URL has already been added")
                    return@launch
                }

                // Process categories
                val categoryIds = categoryNames.map { categoryName ->
                    categoryRepository.getOrCreateCategory(categoryName.trim()).id
                }

                // Create and insert new link
                val newLink = Link(
                    name = name,
                    uri = uri,
                    categoryIds = categoryIds,
                )
                linkRepository.insertLink(newLink)
                _uiState.value = AddLinkUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddLinkUiState.Error("Failed to add link: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = AddLinkUiState.Initial
    }

    fun resetEditState() {
        _editLinkUiState.value = EditLinkUiState.Initial
    }

    fun editLink(link: Link, name: String, url: String, categoryNames: List<String> = emptyList()) {
        viewModelScope.launch(dispatcher) {
            _editLinkUiState.value = EditLinkUiState.Loading

            try {
                if (name.isBlank()) {
                    _editLinkUiState.value = EditLinkUiState.Error("Name is required")
                    return@launch
                }

                if (url.isBlank()) {
                    _editLinkUiState.value = EditLinkUiState.Error("URL is required")
                    return@launch
                }

                if (!UrlValidator.isValid(url)) {
                    _editLinkUiState.value = EditLinkUiState.Error(
                        "Invalid URL format. URL must be a valid http:// or https:// address",
                    )
                    return@launch
                }

                val uri = URI(url)

                // Check if URL already exists and it's not the same link
                val existingLink = linkRepository.getLinkByUri(uri)
                if (existingLink != null && existingLink.id != link.id) {
                    _editLinkUiState.value = EditLinkUiState.Error("This URL has already been added")
                    return@launch
                }

                // Process categories
                val categoryIds = categoryNames.map { categoryName ->
                    categoryRepository.getOrCreateCategory(categoryName.trim()).id
                }

                // Create and update link
                val updatedLink = link.copy(
                    name = name,
                    uri = uri,
                    categoryIds = categoryIds,
                    updated = java.time.Instant.now(),
                )
                linkRepository.updateLink(updatedLink)
                _editLinkUiState.value = EditLinkUiState.Success
            } catch (e: Exception) {
                _editLinkUiState.value = EditLinkUiState.Error("Failed to update link: ${e.message}")
            }
        }
    }

    suspend fun getCategoriesForLink(link: Link): List<Category> = link.categoryIds.mapNotNull { categoryId ->
        categoryRepository.getCategoryById(categoryId)
    }

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
                // Automatically select the newly created category
                selectCategory(newCategory)
                _categoryUiState.value = CategoryUiState.Success
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding category", e)
                _categoryUiState.value = CategoryUiState.Error("Failed to add category: ${e.message}")
            }
        }
    }

    fun resetCategoryState() {
        _categoryUiState.value = CategoryUiState.Initial
    }

    fun selectCategory(category: Category) {
        val currentCategories = _selectedCategories.value.toMutableList()
        if (!currentCategories.contains(category)) {
            currentCategories.add(category)
            _selectedCategories.value = currentCategories
        }
    }

    fun unselectCategory(category: Category) {
        val currentCategories = _selectedCategories.value.toMutableList()
        currentCategories.remove(category)
        _selectedCategories.value = currentCategories
    }

    fun clearSelectedCategories() {
        _selectedCategories.value = emptyList()
    }

    fun selectCategoryFilter(categoryId: String?) {
        if (categoryId == null) {
            // "All" category selected - clear all selections
            _selectedCategoryIds.value = emptyList()
            _currentFilter.value = LinkFilter.ALL
        } else {
            // Check if this category is already selected
            val currentSelection = _selectedCategoryIds.value.toMutableList()

            if (currentSelection.contains(categoryId)) {
                // If already selected, remove it
                currentSelection.remove(categoryId)
            } else {
                // If not selected, add it
                currentSelection.add(categoryId)
            }

            _selectedCategoryIds.value = currentSelection

            // If no categories are selected after update, switch back to ALL filter
            if (currentSelection.isEmpty()) {
                _currentFilter.value = LinkFilter.ALL
            } else {
                _currentFilter.value = LinkFilter.CATEGORY
            }
        }
    }

    fun deleteLink(link: Link) {
        pendingDeletingLink = link
        viewModelScope.launch(dispatcher) {
            linkRepository.deleteLink(link.id)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            pendingDeletingLink?.let { link ->
                addLink(
                    name = link.name?.trim() ?: "Unnamed",
                    url = link.uri.toString(),
                    categoryNames = link.categoryIds.map { categoryId ->
                        categoryRepository.getCategoryById(categoryId)?.name ?: "No Category"
                    },
                )
            }
        }
    }

    fun hideSnackbar() {
        _snackbarState.value = SnackbarState.Hidden
    }

    fun setFilter(filter: LinkFilter) {
        _currentFilter.value = filter
        // Reset selected categories if not filtering by category
        if (filter != LinkFilter.CATEGORY) {
            _selectedCategoryIds.value = emptyList()
        }
    }

    fun markLinkAsVisited(link: Link) {
        viewModelScope.launch(dispatcher) {
            try {
                linkRepository.markLinkAsVisited(link)
            } catch (e: Exception) {
                // Handle error if needed
                Log.e("MainViewModel", "Error marking link as visited", e)
            }
        }
    }

    fun setSharedUrl(url: String) {
        _sharedUrl.value = url
    }

    fun clearSharedUrl() {
        _sharedUrl.value = null
    }

    fun populateTestData() {
        viewModelScope.launch(dispatcher) {
            addCategory("A")
            addCategory("B")
            addCategory("C")
            addLink("A", "https://www.a.com")
            addLink("B", "https://www.b.com")
            addLink("C", "https://www.c.com")
        }
    }
}

sealed class AddLinkUiState {
    object Initial : AddLinkUiState()
    object Loading : AddLinkUiState()
    object Success : AddLinkUiState()
    data class Error(val message: String) : AddLinkUiState()
}

sealed class EditLinkUiState {
    object Initial : EditLinkUiState()
    object Loading : EditLinkUiState()
    object Success : EditLinkUiState()
    data class Error(val message: String) : EditLinkUiState()
}

sealed class CategoryUiState {
    object Initial : CategoryUiState()
    object Loading : CategoryUiState()
    object Success : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

sealed class SnackbarState {
    object Hidden : SnackbarState()
    data class Visible(val message: String, val linkName: String) : SnackbarState()
}
