package com.softklass.linkbarn.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun `isValid should return true for valid HTTP URL`() {
        assertTrue(UrlValidator.isValid("http://example.com"))
    }

    @Test
    fun `isValid should return true for valid HTTPS URL`() {
        assertTrue(UrlValidator.isValid("https://example.com"))
    }

    @Test
    fun `isValid should return true for valid URL with query parameters`() {
        assertTrue(UrlValidator.isValid("https://example.com/path?query=1&a=b"))
    }

    @Test
    fun `isValid should return true for valid URL with fragment`() {
        assertTrue(UrlValidator.isValid("https://example.com/#fragment"))
    }

    @Test
    fun `isValid should return true for valid URL with IP address`() {
        assertTrue(UrlValidator.isValid("https://127.0.0.1"))
    }

    @Test
    fun `isValid should return true for valid URL with localhost`() {
        assertTrue(UrlValidator.isValid("https://localhost"))
    }

    @Test
    fun `isValid should return true for URL without scheme or www`() {
        assertTrue(UrlValidator.isValid("softklass.com"))
    }

    @Test
    fun `formatUrl should format softklass com to https www softklass com`() {
        assertEquals("https://www.softklass.com", UrlValidator.formatUrl("softklass.com"))
    }

    @Test
    fun `formatUrl should prepend https when scheme missing but www present`() {
        assertEquals("https://www.softklass.com", UrlValidator.formatUrl("www.softklass.com"))
    }

    @Test
    fun `formatUrl should prepend www when scheme present but www missing`() {
        assertEquals("https://www.softklass.com", UrlValidator.formatUrl("https://softklass.com"))
        assertEquals("http://www.softklass.com", UrlValidator.formatUrl("http://softklass.com"))
    }

    @Test
    fun `formatUrl should preserve path, query, and fragment`() {
        assertEquals(
            "https://www.softklass.com/path?key=val#section",
            UrlValidator.formatUrl("softklass.com/path?key=val#section"),
        )
    }

    @Test
    fun `formatUrl should not prepend www to localhost or IP address`() {
        assertEquals("https://localhost", UrlValidator.formatUrl("localhost"))
        assertEquals("https://127.0.0.1", UrlValidator.formatUrl("127.0.0.1"))
    }
}
