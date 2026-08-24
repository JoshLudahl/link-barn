package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.ClickedLinkRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import com.softklass.linkbarn.utils.UrlValidator
import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MainViewModelTest {
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
    fun `isValid should return true for valid HTTP URL`() {
        assertTrue(UrlValidator.isValid("http://example.com"))
    }

    @Test
    fun `isValid should return true for valid HTTPS URL`() {
        assertTrue(UrlValidator.isValid("https://example.com"))
    }

    @Test
    fun `isValid should return false for FTP URL`() {
        assertTrue(!UrlValidator.isValid("ftp://example.com"))
    }

    @Test
    fun `isValid should return false for missing hostname`() {
        assertTrue(!UrlValidator.isValid("http://"))
    }

    @Test
    fun `isValid should return false for malformed URL`() {
        assertTrue(!UrlValidator.isValid("not a url"))
    }

    @Test
    fun `isValid should return false for blank URL`() {
        assertTrue(!UrlValidator.isValid("   "))
    }

    @Test
    fun `deleteLink should call repository deleteLink with correct id`() = runTest {
        // Given
        val testLink = Link(
            id = "test-id",
            name = "Test Link",
            uri = URI("https://example.com"),
        )

        // When
        viewModel.deleteLink(testLink)

        // Wait for animation delay to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(linkDataRepository).deleteLink(testLink.id)
    }

    @Test
    fun `setSharedUrl should update sharedUrl state`() = runTest {
        // Given
        val testUrl = "https://example.com"

        // When
        viewModel.setSharedUrl(testUrl)

        // Then
        assertEquals(testUrl, viewModel.sharedUrl.value)
    }

    @Test
    fun `clearSharedUrl should set sharedUrl state to null`() = runTest {
        // Given
        val testUrl = "https://example.com"
        viewModel.setSharedUrl(testUrl)

        // When
        viewModel.clearSharedUrl()

        // Then
        assertNull(viewModel.sharedUrl.value)
    }

    @Test
    fun `sharedUrl should initially be null`() = runTest {
        // Then
        assertNull(viewModel.sharedUrl.value)
    }

    @Test
    fun `links flow should filter by search query`() = runTest {
        // Given
        val link1 = Link(name = "Google", uri = URI("https://google.com"))
        val link2 = Link(name = "Android", uri = URI("https://android.com"))
        val allLinks = listOf(link1, link2)
        whenever(linkDataRepository.getAllLinks()).doReturn(flowOf(allLinks))

        // When
        viewModel.onSearchQueryChanged("goog")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val filteredLinks = viewModel.links.value
        assertEquals(1, filteredLinks.size)
        assertEquals("Google", filteredLinks[0].name)
    }

    @Test
    fun `links flow should be case insensitive`() = runTest {
        // Given
        val link1 = Link(name = "Google", uri = URI("https://google.com"))
        val allLinks = listOf(link1)
        whenever(linkDataRepository.getAllLinks()).doReturn(flowOf(allLinks))

        // When
        viewModel.onSearchQueryChanged("GOOGLE")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val filteredLinks = viewModel.links.value
        assertEquals(1, filteredLinks.size)
    }
}
