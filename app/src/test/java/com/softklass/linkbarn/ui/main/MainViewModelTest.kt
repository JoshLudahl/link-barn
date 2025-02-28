package com.softklass.linkbarn.ui.main

import com.softklass.linkbarn.data.repository.LinkDataRepository
import com.softklass.linkbarn.utils.UrlValidator
import com.softklass.linkbarn.data.model.Link
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.verify
import java.net.URI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private val linkDataRepository: LinkDataRepository = mock()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(linkDataRepository)
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
            uri = URI("https://example.com")
        )

        // When
        viewModel.deleteLink(testLink)

        // Then
        verify(linkDataRepository).deleteLink(testLink.id)
    }
}
