package com.softklass.linkbarn.data.db

import androidx.room.TypeConverter
import java.net.URI
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromUriString(value: String?): URI? = value?.let { URI.create(it) }

    @TypeConverter
    fun uriToString(uri: URI?): String? = uri?.toString()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.joinToString(",")

    @TypeConverter
    fun toStringList(value: String?): List<String> = if (value.isNullOrEmpty()) emptyList() else value.split(",")
}
