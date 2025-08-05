package com.softklass.linkbarn.di

import android.content.Context
import androidx.room.Room
import com.softklass.linkbarn.data.db.AppDatabase
import com.softklass.linkbarn.data.db.dao.CategoryDao
import com.softklass.linkbarn.data.db.dao.ClickedLinkDao
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.preferences.SettingsPreferences
import com.softklass.linkbarn.data.repository.ClickedLinkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val DATABASE_NAME = "link_barn.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        DATABASE_NAME,
    ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        .fallbackToDestructiveMigration(false) // During development, we'll allow destructive migrations
        .build()

    @Provides
    @Singleton
    fun provideLinkDao(database: AppDatabase): LinkDao = database.linkDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideClickedLinkDao(database: AppDatabase): ClickedLinkDao = database.clickedLinkDao()

    @Provides
    @Singleton
    fun provideClickedLinkRepository(
        clickedLinkDao: ClickedLinkDao,
        linkDao: LinkDao,
    ): ClickedLinkRepository = ClickedLinkRepository(clickedLinkDao, linkDao)

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): SettingsPreferences = SettingsPreferences(context)
}
