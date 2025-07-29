package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.MainCoroutineRule
import com.softklass.linkbarn.data.model.Link
import com.softklass.linkbarn.data.repository.CategoryRepository
import com.softklass.linkbarn.data.repository.LinkDataRepository
import com.softklass.linkbarn.utils.UrlValidator
import java.net.URI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

    private val linkDataRepository: LinkDataRepository = mock()
    private val categoryRepository: CategoryRepository = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
            linkDataRepository,
            categoryRepository = categoryRepository,
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
}
