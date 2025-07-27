package com.softklass.linkbarn.di

import android.content.Context
import androidx.room.Room
import com.softklass.linkbarn.data.db.AppDatabase
import com.softklass.linkbarn.data.db.dao.LinkDao
import com.softklass.linkbarn.data.preferences.SettingsPreferences
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
    ).fallbackToDestructiveMigration(false) // During development, we'll allow destructive migrations
        .build()

    @Provides
    @Singleton
    fun provideLinkDao(database: AppDatabase): LinkDao = database.linkDao()

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideSettingsPreferences(@ApplicationContext context: Context): SettingsPreferences = SettingsPreferences(context)
}
