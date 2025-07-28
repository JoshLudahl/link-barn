package com.softklass.linkbarn.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @Mock
    private lateinit var repository: LinkDataRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var viewModel: TestMainViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = TestMainViewModel(repository, categoryRepository, testDispatcher)
    }

    @Test
    fun addLink_emptyName_returnsError() = runTest {
        // When
        viewModel.addLink("", "https://example.com")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is AddLinkUiState.Error)
        assertEquals("Name is required", (state as AddLinkUiState.Error).message)
    }

    @Test
    fun addLink_emptyUrl_returnsError() = runTest {
        // When
        viewModel.addLink("Test Link", "")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is AddLinkUiState.Error)
        assertEquals("URL is required", (state as AddLinkUiState.Error).message)
    }

    @Test
    fun addLink_invalidUrl_returnsError() = runTest {
        // When
        viewModel.addLink("Test Link", "not a url")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is AddLinkUiState.Error)
        assertEquals("Invalid URL format. URL must be a valid http:// or https:// address", (state as AddLinkUiState.Error).message)
    }

    @Test
    fun addLink_duplicateUrl_returnsError() = runTest {
        // Given
        val uri = URI("https://example.com")
        val existingLink = Link(name = "Existing Link", uri = uri)

        // Mock the repository to return an existing link for the test URI
        kotlinx.coroutines.runBlocking {
            whenever(repository.getLinkByUri(uri)).thenReturn(existingLink)
        }

        // When
        viewModel.addLink("Test Link", "https://example.com")

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is AddLinkUiState.Error)
        assertEquals("This URL has already been added", (state as AddLinkUiState.Error).message)
    }

    @Test
    fun addLink_success() = runTest {
        // Given
        val uri = URI("https://example.com")
        whenever(repository.getLinkByUri(uri)).thenReturn(null)

        // When
        viewModel.addLink("Test Link", "https://example.com")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is AddLinkUiState.Success)
    }
}
