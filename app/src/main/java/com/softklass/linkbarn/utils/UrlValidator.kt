package com.softklass.linkbarn.utils

import android.util.Log
import java.net.URI
import java.util.regex.Pattern

object UrlValidator {
    // Regular expression for basic URL validation (simplified version of Patterns.WEB_URL)
    private val WEB_URL_PATTERN = Pattern.compile(
        "^(https?://)" + // scheme
            "([a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,})" + // hostname
            "(:\\d{1,5})?" + // port
            "(/[a-zA-Z0-9\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*)?" + // path
            "$",
    )

    // Flag to detect if we're in a test environment
    private val inTestEnvironment = detectTestEnvironment()

    /**
     * Validates if the given string is a valid HTTP/HTTPS URL.
     *
     * @param url The URL string to validate
     * @return true if the URL is valid, false otherwise
     */
    fun isValid(url: String): Boolean {
        if (url.isBlank()) return false

        return try {
            // Use our custom pattern matcher that works in both environments
            if (!WEB_URL_PATTERN.matcher(url).matches()) {
                return false
            }

            val uri = URI(url)
            val hasValidScheme = uri.scheme?.equals("http", true) == true || uri.scheme?.equals("https", true) == true
            val hasValidHost = !uri.host.isNullOrBlank()

            // In test environment, we don't use Android's URLUtil
            hasValidScheme && hasValidHost
        } catch (e: Exception) {
            // Use a safe logging approach that works in both app and test environments
            try {
                Log.e("UrlValidator", "Error validating URL: ${e.message}")
            } catch (ignored: Exception) {
                // In test environment, Log might not be available
                println("UrlValidator: Error validating URL: ${e.message}")
            }
            false
        }
    }

    /**
     * Detects if we're running in a test environment by checking
     * if Android-specific classes are available.
     */
    private fun detectTestEnvironment(): Boolean = try {
        // Try to access an Android-specific class method
        Class.forName("android.webkit.URLUtil")
        Class.forName("android.util.Patterns")
        false // Not in test environment
    } catch (e: Exception) {
        true // In test environment
    }
}
