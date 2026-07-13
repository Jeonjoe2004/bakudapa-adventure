package com.bakudapa.adventure.feature.home.domain.usecase

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.model.HomeData
import com.bakudapa.adventure.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<DataResult<HomeData>> = repository.getHomeData()
}
