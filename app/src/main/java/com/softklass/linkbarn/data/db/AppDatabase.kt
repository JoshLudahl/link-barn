package com.softklass.linkbarn.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.softklass.linkbarn.data.db.dao.CategoryDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.Link

@Database(
    entities = [
        Link::class,
        Category::class,
    ],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the categories table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `categories` (
                        `id` TEXT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """,
                )

                // Add categoryIds column to links table
                db.execSQL("ALTER TABLE links ADD COLUMN categoryIds TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
