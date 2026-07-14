package com.bakudapa.adventure.feature.tracking.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import kotlinx.coroutines.flow.Flow

interface TrackingRepository {
    val trackingStatus: Flow<TrackingStatus>
    val currentRoute: Flow<HikingRoute>

    suspend fun startTracking()
    suspend fun pauseTracking()
    suspend fun resumeTracking()
    suspend fun stopTracking()

    suspend fun addTrackingPoint(point: TrackingPoint)

    suspend fun saveRoute(route: HikingRoute): DataResult<Unit>
    fun getSavedRoutes(): Flow<DataResult<List<HikingRoute>>>

    suspend fun exportToGpx(route: HikingRoute): DataResult<String>
}
