package com.bakudapa.adventure.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteEntity
import com.bakudapa.adventure.feature.tracking.data.local.TrackingTypeConverters

@Database(
    entities = [HikingRouteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TrackingTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hikingRouteDao(): HikingRouteDao
}
