package com.softklass.linkbarn.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softklass.linkbarn.data.model.Link
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LinkFilter {
    ALL,
    VISITED,
    UNVISITED,
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val linkRepository: LinkDataRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddLinkUiState>(AddLinkUiState.Initial)
    val uiState: StateFlow<AddLinkUiState> = _uiState

    private val _currentFilter = MutableStateFlow(LinkFilter.ALL)
    val currentFilter: StateFlow<LinkFilter> = _currentFilter

    // Track all links separately to determine if we should show segmented buttons
    val allLinks: StateFlow<List<Link>> = linkRepository.getAllLinks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val links: StateFlow<List<Link>> = _currentFilter.flatMapLatest { filter ->
        when (filter) {
            LinkFilter.ALL -> linkRepository.getAllLinks()
            LinkFilter.VISITED -> linkRepository.getVisitedLinks()
            LinkFilter.UNVISITED -> linkRepository.getUnvisitedLinks()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun addLink(name: String, url: String) {
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

                // Create and insert new link
                val newLink = Link(
                    name = name,
                    uri = uri,
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

    fun deleteLink(link: Link) {
        viewModelScope.launch(dispatcher) {
            try {
                linkRepository.deleteLink(link.id)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun setFilter(filter: LinkFilter) {
        _currentFilter.value = filter
    }

    fun markLinkAsVisited(link: Link) {
        viewModelScope.launch(dispatcher) {
            try {
                linkRepository.markLinkAsVisited(link)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}

sealed class AddLinkUiState {
    object Initial : AddLinkUiState()
    object Loading : AddLinkUiState()
    object Success : AddLinkUiState()
    data class Error(val message: String) : AddLinkUiState()
}
