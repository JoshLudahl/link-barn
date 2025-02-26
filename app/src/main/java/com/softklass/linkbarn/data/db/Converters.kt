package com.softklass.linkbarn.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Label
import com.softklass.linkbarn.data.model.Status
import java.net.URI
import java.time.Instant

class Converters {
    private val gson = Gson()

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

    @TypeConverter
    fun fromStatus(value: Status?): String? {
        return value?.name
    }

    @TypeConverter
    fun toStatus(value: String?): Status? {
        return value?.let { Status.valueOf(it) }
    }

    @TypeConverter
    fun fromCategoryList(value: List<Category?>?): String? {
        val type = object : TypeToken<List<Category>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCategoryList(value: String?): List<Category?>? {
        val type = object : TypeToken<List<Category>>() {}.type
        return value?.let { gson.fromJson(it, type) }
    }

    @TypeConverter
    fun fromLabelList(value: List<Label?>?): String? {
        val type = object : TypeToken<List<Label>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toLabelList(value: String?): List<Label?>? {
        val type = object : TypeToken<List<Label>>() {}.type
        return value?.let { gson.fromJson(it, type) }
    }
}
