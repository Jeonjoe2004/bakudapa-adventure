package com.bakudapa.adventure.di

import android.content.Context
import androidx.room.Room
import com.bakudapa.adventure.data.local.AppDatabase
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bakudapa_adventure_db"
        ).build()
    }

    @Provides
    fun provideHikingRouteDao(database: AppDatabase): HikingRouteDao {
        return database.hikingRouteDao()
    }
}
