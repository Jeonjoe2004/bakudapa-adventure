package com.bakudapa.adventure.feature.mountain.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDetail
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import kotlinx.coroutines.flow.Flow

interface MountainRepository {
    fun getMountainDetail(mountainId: String): Flow<DataResult<MountainDetail>>
    fun getTrails(mountainId: String): Flow<DataResult<List<TrailInfo>>>
}
