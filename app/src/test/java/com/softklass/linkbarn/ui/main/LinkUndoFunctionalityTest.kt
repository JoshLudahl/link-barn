package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.ClickedLinkRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class LinkUndoFunctionalityTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    private val linkDataRepository: LinkDataRepository = mock()
    private val categoryRepository: CategoryRepository = mock()
    private val clickedLinkRepository: ClickedLinkRepository = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
            linkDataRepository,
            categoryRepository = categoryRepository,
            clickedLinkRepository = clickedLinkRepository,
            dispatcher = testDispatcher,
        )
    }

    @Test
    fun `deleteLink should add link to pending deletions and show snackbar`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        // When
        viewModel.deleteLink(testLink)

        // Then
        assertTrue(
            "Link should be in pending deletions",
            viewModel.pendingDeletions.value.contains(testLink.id),
        )

        val snackbarState = viewModel.snackbarState.value
        assertTrue(
            "Snackbar should be visible",
            snackbarState is SnackbarState.Visible,
        )

        if (snackbarState is SnackbarState.Visible) {
            assertEquals("Link deleted", snackbarState.message)
            assertEquals("Test Link", snackbarState.linkName)
        }
    }

    @Test
    fun `deleteLink should not immediately delete from repository`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        // When
        viewModel.deleteLink(testLink)

        // Advance time but not the full 5 seconds
        testDispatcher.scheduler.advanceTimeBy(1000) // 1 second

        // Then
        verify(linkDataRepository, never()).deleteLink(testLink.id)
    }

    @Test
    fun `undoDelete should remove link from pending deletions and hide snackbar`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        viewModel.deleteLink(testLink)

        // Verify initial state
        assertTrue(
            "Link should be in pending deletions",
            viewModel.pendingDeletions.value.contains(testLink.id),
        )
        assertTrue(
            "Snackbar should be visible",
            viewModel.snackbarState.value is SnackbarState.Visible,
        )

        // When
        viewModel.undoDelete()

        // Then
        assertTrue(
            "Link should not be in pending deletions",
            !viewModel.pendingDeletions.value.contains(testLink.id),
        )
        assertTrue(
            "Snackbar should be hidden",
            viewModel.snackbarState.value is SnackbarState.Hidden,
        )

        // Advance time to ensure delete job was cancelled
        testDispatcher.scheduler.advanceUntilIdle()
        verify(linkDataRepository, never()).deleteLink(testLink.id)
    }

    @Test
    fun `deleteLink should eventually delete from repository after delay`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        // When
        viewModel.deleteLink(testLink)

        // Advance time to complete the 5-second delay
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(linkDataRepository).deleteLink(testLink.id)
        assertTrue(
            "Link should not be in pending deletions",
            !viewModel.pendingDeletions.value.contains(testLink.id),
        )
        assertTrue(
            "Snackbar should be hidden",
            viewModel.snackbarState.value is SnackbarState.Hidden,
        )
    }

    @Test
    fun `hideSnackbar should hide snackbar`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        viewModel.deleteLink(testLink)
        assertTrue(
            "Snackbar should be visible",
            viewModel.snackbarState.value is SnackbarState.Visible,
        )

        // When
        viewModel.hideSnackbar()

        // Then
        assertTrue(
            "Snackbar should be hidden",
            viewModel.snackbarState.value is SnackbarState.Hidden,
        )
    }
}
