package com.softklass.linkbarn.data.model

import androidx.room.ColumnInfo

data class LinkClickCount(
    @ColumnInfo(name = "link_id")
    val linkId: String,

    @ColumnInfo(name = "click_count")
    val clickCount: Int,
)
