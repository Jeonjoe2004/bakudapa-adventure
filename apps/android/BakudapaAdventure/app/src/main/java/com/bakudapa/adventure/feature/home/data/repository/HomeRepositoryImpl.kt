package com.bakudapa.adventure.feature.home.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.model.*
import com.bakudapa.adventure.feature.home.domain.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor() : HomeRepository {

    override fun getHomeData(): Flow<DataResult<HomeData>> = flow {
        emit(DataResult.Loading)
        delay(1000) // Simulate network delay
        
        val mockData = HomeData(
            recommendedMountains = listOf(
                Mountain("1", "Mount Klabat", "Minahasa Utara", 1995, "https://example.com/klabat.jpg", 4.8f),
                Mountain("2", "Mount Lokon", "Tomohon", 1580, "https://example.com/lokon.jpg", 4.5f)
            ),
            popularTrails = listOf(
                Trail("1", "Klabat Peak Trail", "Mount Klabat", TrailDifficulty.MODERATE, 360, 5.5, "https://example.com/trail1.jpg"),
                Trail("2", "Lokon Crater Path", "Mount Lokon", TrailDifficulty.EASY, 120, 2.3, "https://example.com/trail2.jpg")
            ),
            latestPosts = listOf(
                Post("1", "John Doe", "https://example.com/user1.jpg", "The sunrise at Klabat was amazing!", null, System.currentTimeMillis(), 124, 8)
            ),
            nearbyMountains = listOf(
                Mountain("3", "Mount Mahawu", "Tomohon", 1324, "https://example.com/mahawu.jpg", 4.2f, 12.5)
            ),
            weather = Weather(24.5f, "Cloudy", "https://example.com/weather.png", 80, 5.5f)
        )
        emit(DataResult.Success(mockData))
    }

    override suspend fun searchMountains(query: String): DataResult<List<Mountain>> {
        delay(500)
        // Mock search logic
        return DataResult.Success(emptyList())
    }
}
