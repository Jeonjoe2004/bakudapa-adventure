package com.bakudapa.adventure.feature.map.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import com.bakudapa.adventure.feature.map.domain.model.MarkerType
import com.bakudapa.adventure.feature.map.domain.repository.MapRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepositoryImpl @Inject constructor() : MapRepository {

    override fun getMapMarkers(): Flow<DataResult<List<MapMarker>>> = flow {
        emit(DataResult.Loading)
        delay(1000)
        val mockMarkers = listOf(
            MapMarker("1", "Klabat Summit", "Highest peak", 1.45, 125.0, MarkerType.SUMMIT, 1995),
            MapMarker("2", "Lokon Crater", "Active", 1.35, 124.8, MarkerType.DANGER_ZONE, 1580)
        )
        emit(DataResult.Success(mockMarkers))
    }

    override suspend fun downloadMapRegion(regionName: String): DataResult<Unit> {
        delay(2000)
        return DataResult.Success(Unit)
    }
}
