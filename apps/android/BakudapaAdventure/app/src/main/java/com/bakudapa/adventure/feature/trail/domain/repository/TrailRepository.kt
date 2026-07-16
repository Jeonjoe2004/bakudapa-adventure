package com.bakudapa.adventure.feature.trail.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import kotlinx.coroutines.flow.Flow

interface TrailRepository {
    fun getTrailDetail(trailId: String): Flow<DataResult<TrailDetail>>
    fun getOtherTrails(mountainId: String, excludeTrailId: String): Flow<DataResult<List<TrailInfo>>>
}
