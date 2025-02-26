package com.softklass.linkbarn.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "namespace")
    val namespace: String = "Label",

    @ColumnInfo(name = "label")
    val label: String
)
