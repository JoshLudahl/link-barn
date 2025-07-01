package com.softklass.linkbarn.di

import android.content.Context
import androidx.room.Room
import com.softklass.linkbarn.data.db.AppDatabase
import com.softklass.linkbarn.data.db.dao.LinkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val DATABASE_NAME = "link_barn.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME,
        ).fallbackToDestructiveMigration(false) // During development, we'll allow destructive migrations
            .build()
    }

    @Provides
    @Singleton
    fun provideLinkDao(database: AppDatabase): LinkDao {
        return database.linkDao()
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
