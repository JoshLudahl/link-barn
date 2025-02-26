package com.softklass.linkbarn.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "namespace")
    val namespace: String = "Category",

    @ColumnInfo(name = "category")
    val category: String
)
