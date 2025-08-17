package com.softklass.linkbarn.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.softklass.linkbarn.data.db.Converters
import java.net.URI
import java.time.Instant
import java.util.UUID

@Entity(tableName = "links")
@TypeConverters(Converters::class)
data class Link(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "updated")
    val updated: Instant = Instant.now(),

    @ColumnInfo(name = "created")
    val created: Instant = Instant.now(),

    @ColumnInfo(name = "uri")
    val uri: URI,

    @ColumnInfo(name = "visited")
    val visited: Boolean = false,

    @ColumnInfo(name = "categoryIds")
    val categoryIds: List<String> = emptyList(),
)
