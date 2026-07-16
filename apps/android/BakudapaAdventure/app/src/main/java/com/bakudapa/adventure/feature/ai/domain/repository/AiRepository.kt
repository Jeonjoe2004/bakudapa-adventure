package com.bakudapa.adventure.feature.ai.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.ai.domain.model.RouteRecommendation
import com.bakudapa.adventure.feature.ai.domain.model.SafetyAlert
import com.bakudapa.adventure.feature.ai.domain.model.TrailAnalysis
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    fun getRecommendations(userId: String? = null): Flow<DataResult<List<RouteRecommendation>>>
    suspend fun getTrailAnalysis(trailId: String): DataResult<TrailAnalysis>
    suspend fun getSafetyAlerts(trailId: String, currentHour: Int, distanceKm: Double, elevation: Int): List<SafetyAlert>
    suspend fun getWeatherForecast(latitude: Double, longitude: Double): DataResult<WeatherForecast>
}

data class WeatherForecast(
    val temperature: Float = 24f,
    val feelsLike: Float = 22f,
    val humidity: Int = 80,
    val windSpeed: Float = 5f,
    val condition: String = "Cloudy",
    val icon: String = "04d",
    val rainChance: Float = 0.3f,
    val hourForecast: List<HourlyForecast> = emptyList(),
)

data class HourlyForecast(
    val hour: Int,
    val temperature: Float,
    val condition: String,
    val rainChance: Float,
)
