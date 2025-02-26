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
    @ColumnInfo(name = "categories")
    val categories: List<Category?> = listOf(),

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "labels")
    val labels: List<Label?> = listOf(),

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "namespace")
    val namespace: String = "Link",

    @ColumnInfo(name = "status")
    val status: Status = Status.UNREAD,

    @ColumnInfo(name = "updated")
    val updated: Instant = Instant.now(),

    @ColumnInfo(name = "uri")
    val uri: URI,

    @ColumnInfo(name = "visited")
    val visited: Boolean = false,
)
