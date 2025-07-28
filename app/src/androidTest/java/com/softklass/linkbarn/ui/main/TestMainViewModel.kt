package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import com.softklass.linkbarn.utils.UrlValidator
import java.net.URI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A simplified version of MainViewModel for testing purposes.
 * This class only implements the functionality needed for the tests.
 */
class TestMainViewModel(
    private val linkRepository: LinkDataRepository,
    private val categoryRepository: CategoryRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _uiState = MutableStateFlow<AddLinkUiState>(AddLinkUiState.Initial)
    val uiState: StateFlow<AddLinkUiState> = _uiState

    // Simplified implementation that only handles the functionality needed for tests
    fun addLink(name: String, url: String, categoryNames: List<String> = emptyList()) {
        kotlinx.coroutines.runBlocking {
            if (name.isBlank()) {
                _uiState.value = AddLinkUiState.Error("Name is required")
                return@runBlocking
            }

            if (url.isBlank()) {
                _uiState.value = AddLinkUiState.Error("URL is required")
                return@runBlocking
            }

            if (!UrlValidator.isValid(url)) {
                _uiState.value = AddLinkUiState.Error("Invalid URL format. URL must be a valid http:// or https:// address")
                return@runBlocking
            }

            try {
                val uri = URI(url)

                // Check if URL already exists
                if (linkRepository.getLinkByUri(uri) != null) {
                    _uiState.value = AddLinkUiState.Error("This URL has already been added")
                    return@runBlocking
                }

                _uiState.value = AddLinkUiState.Success
            } catch (e: Exception) {
                _uiState.value = AddLinkUiState.Error("Failed to add link: ${e.message}")
            }
        }
    }
}
