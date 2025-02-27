package com.softklass.linkbarn.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Link

@Database(
    entities = [
        Link::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
}
