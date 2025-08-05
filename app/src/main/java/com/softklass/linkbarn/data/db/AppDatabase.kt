package com.softklass.linkbarn.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.softklass.linkbarn.data.db.dao.CategoryDao
import com.softklass.linkbarn.data.db.dao.ClickedLinkDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.model.Category
import com.softklass.linkbarn.data.model.ClickedLink
import com.softklass.linkbarn.data.model.Link

@Database(
    entities = [
        Link::class,
        Category::class,
        ClickedLink::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
    abstract fun categoryDao(): CategoryDao
    abstract fun clickedLinkDao(): ClickedLinkDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create the clicked_links table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `clicked_links` (
                        `id` TEXT NOT NULL, 
                        `link_id` TEXT NOT NULL, 
                        `clicked_at` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """,
                )
            }
        }
    }
}
