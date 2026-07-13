package com.bakudapa.adventure.feature.home.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.model.HomeData
import com.bakudapa.adventure.feature.home.domain.model.Mountain
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeData(): Flow<DataResult<HomeData>>
    suspend fun searchMountains(query: String): DataResult<List<Mountain>>
}
