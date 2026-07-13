package com.bakudapa.adventure.feature.home.domain.usecase

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.model.Mountain
import com.bakudapa.adventure.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class SearchMountainsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(query: String): DataResult<List<Mountain>> {
        if (query.isBlank()) return DataResult.Success(emptyList())
        return repository.searchMountains(query)
    }
}
