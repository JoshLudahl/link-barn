package com.softklass.linkbarn.utils

import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import java.net.URI

object UrlValidator {
    /**
     * Validates if the given string is a valid HTTP/HTTPS URL.
     *
     * @param url The URL string to validate
     * @return true if the URL is valid, false otherwise
     */
    fun isValid(url: String): Boolean {
        if (url.isBlank()) return false

        return try {
            if (!Patterns.WEB_URL.matcher(url).matches()) {
                return false
            }

            val uri = URI(url)
            val hasValidScheme = uri.scheme?.equals("http", true) == true || uri.scheme?.equals("https", true) == true
            val hasValidHost = !uri.host.isNullOrBlank()

            val isValidUrl = URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)

            hasValidScheme && hasValidHost && isValidUrl
        } catch (e: Exception) {
            Log.e("UrlValidator", "Error validating URL: ${e.message}")
            false
        }
    }
}
