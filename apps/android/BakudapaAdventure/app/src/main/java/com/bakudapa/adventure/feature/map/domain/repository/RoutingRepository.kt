package com.bakudapa.adventure.feature.map.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.domain.model.Route
import kotlinx.coroutines.flow.Flow

interface RoutingRepository {
    suspend fun getRoute(
        originLat: Double,
        originLng: Double,
        destLat: Double,
        destLng: Double
    ): DataResult<com.bakudapa.adventure.feature.map.domain.model.Route>
}