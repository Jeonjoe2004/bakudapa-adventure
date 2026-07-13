package com.bakudapa.adventure.feature.tracking.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HikingRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: HikingRouteEntity)

    @Query("SELECT * FROM hiking_routes ORDER BY startTime DESC")
    fun getAllRoutes(): Flow<List<HikingRouteEntity>>

    @Delete
    suspend fun deleteRoute(route: HikingRouteEntity)
}
