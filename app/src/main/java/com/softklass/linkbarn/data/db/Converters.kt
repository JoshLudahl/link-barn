package com.softklass.linkbarn.data.db

import androidx.room.TypeConverter
import java.net.URI
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun fromUriString(value: String?): URI? {
        return value?.let { URI.create(it) }
    }

    @TypeConverter
    fun uriToString(uri: URI?): String? {
        return uri?.toString()
    }
}
