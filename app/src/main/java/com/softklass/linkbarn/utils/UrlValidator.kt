package com.softklass.linkbarn.utils

import java.net.URI

object UrlValidator {
    fun isValid(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.scheme != null && (uri.scheme.equals("http", true) || uri.scheme.equals("https", true)) &&
                !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }
}
