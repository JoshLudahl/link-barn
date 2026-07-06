package com.softklass.linkbarn.utils

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
        // This is known to fail with the current regex
        assertTrue(UrlValidator.isValid("https://127.0.0.1"))
    }

    @Test
    fun `isValid should return true for valid URL with localhost`() {
        assertTrue(UrlValidator.isValid("https://localhost"))
    }
}
