package com.bakudapa.adventure.feature.map.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getMapMarkers(): Flow<DataResult<List<MapMarker>>>
    suspend fun downloadMapRegion(regionName: String): DataResult<Unit>
}
