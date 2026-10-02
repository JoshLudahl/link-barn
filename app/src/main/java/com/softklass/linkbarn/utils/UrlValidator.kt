package com.softklass.linkbarn.utils

import android.util.Log
import java.net.URI
import java.util.regex.Pattern

object UrlValidator {
    // Regular expression for basic URL validation (simplified version of Patterns.WEB_URL)
    private val WEB_URL_PATTERN = Pattern.compile(
        "^(https?://)" + // scheme
            "([a-zA-Z0-9\\-\\.]+)" + // hostname
            "(:\\d{1,5})?" + // port
            "(/[a-zA-Z0-9\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*)?" + // path
            "$",
    )

    private val IP_ADDRESS_PATTERN = Pattern.compile(
        "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$",
    )

    // Flag to detect if we're in a test environment
    private val inTestEnvironment = detectTestEnvironment()

    /**
     * Formats the given URL string by adding a default scheme (https://) if missing
     * and defaulting the host to www. prefix if missing.
     *
     * @param url The URL string to format
     * @return The formatted URL string
     */
    fun formatUrl(url: String): String {
        var formatted = url.trim()
        if (formatted.isBlank()) return formatted

        val hasHttpScheme = formatted.startsWith("http://", ignoreCase = true) ||
            formatted.startsWith("https://", ignoreCase = true)

        if (!hasHttpScheme) {
            if (!formatted.contains("://")) {
                formatted = "https://$formatted"
            }
        }

        return try {
            val uri = URI(formatted)
            val host = uri.host

            if (host != null &&
                !host.startsWith("www.", ignoreCase = true) &&
                !IP_ADDRESS_PATTERN.matcher(host).matches() &&
                !host.equals("localhost", ignoreCase = true)
            ) {
                URI(
                    uri.scheme,
                    uri.userInfo,
                    "www.$host",
                    uri.port,
                    uri.rawPath,
                    uri.rawQuery,
                    uri.rawFragment,
                ).toString()
            } else {
                formatted
            }
        } catch (e: Exception) {
            formatted
        }
    }

    /**
     * Validates if the given string is a valid HTTP/HTTPS URL (formatting it first if needed).
     *
     * @param url The URL string to validate
     * @return true if the URL is valid, false otherwise
     */
    fun isValid(url: String): Boolean {
        if (url.isBlank()) return false
        val formatted = formatUrl(url)

        return try {
            // Use our custom pattern matcher that works in both environments
            if (!WEB_URL_PATTERN.matcher(formatted).matches()) {
                return false
            }

            val uri = URI(formatted)
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
