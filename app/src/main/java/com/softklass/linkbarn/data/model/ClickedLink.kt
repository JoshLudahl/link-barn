package com.softklass.linkbarn.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.softklass.linkbarn.data.db.Converters
import java.time.Instant
import java.util.UUID

@Entity(tableName = "clicked_links")
@TypeConverters(Converters::class)
data class ClickedLink(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "link_id")
    val linkId: String,

    @ColumnInfo(name = "clicked_at")
    val clickedAt: Instant = Instant.now(),
)
