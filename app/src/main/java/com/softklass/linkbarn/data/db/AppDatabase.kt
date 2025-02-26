package com.softklass.linkbarn.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Label
import com.softklass.linkbarn.data.model.Link

@Database(
    entities = [
        Link::class,
        Category::class,
        Label::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // DAOs will be added here
}
