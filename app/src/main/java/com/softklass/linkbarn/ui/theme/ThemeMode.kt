package com.softklass.linkbarn.ui.theme

/**
 * Represents the available theme modes for the application.
 */
enum class ThemeMode(val value: Int) {
    LIGHT(0),
    DARK(1),
    SYSTEM(2),
    ;

    companion object {
        /**
         * Convert an integer value to a ThemeMode.
         * @param value The integer value to convert.
         * @return The corresponding ThemeMode, or SYSTEM if the value is invalid.
         */
        fun fromValue(value: Int): ThemeMode = entries.find { it.value == value } ?: SYSTEM
    }
}
