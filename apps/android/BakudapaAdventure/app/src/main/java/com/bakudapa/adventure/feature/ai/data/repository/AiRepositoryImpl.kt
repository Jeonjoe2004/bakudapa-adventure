package com.bakudapa.adventure.feature.ai.data.repository

import com.bakudapa.adventure.BuildConfig
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.ai.domain.model.*
import com.bakudapa.adventure.feature.ai.domain.repository.AiRepository
import com.bakudapa.adventure.feature.ai.domain.repository.HourlyForecast
import com.bakudapa.adventure.feature.ai.domain.repository.WeatherForecast
import com.bakudapa.adventure.feature.home.data.remote.WeatherApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val weatherApi: WeatherApi
) : AiRepository {

    private val isWeatherKeyReal: Boolean
        get() = BuildConfig.WEATHER_API_KEY.isNotBlank() && BuildConfig.WEATHER_API_KEY != "CHANGE_ME"

    override fun getRecommendations(userId: String?): Flow<DataResult<List<RouteRecommendation>>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val trails = firestoreManager.getCollection("trails")
                .get().await()

            val recommendations = trails.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val mountainName = doc.getString("mountainName") ?: ""
                val duration = doc.getLong("durationMinutes")?.toInt() ?: 0
                val distance = doc.getDouble("distanceKm") ?: 0.0
                val difficulty = doc.getString("difficulty") ?: "MODERATE"
                val elevationGain = doc.getLong("elevationGain")?.toInt() ?: 0
                val rating = doc.getDouble("rating") ?: 3.0
                val popularity = doc.getLong("popularity")?.toInt() ?: 0

                val diffNumeric = when (difficulty) { "EASY" -> 1f; "MODERATE" -> 2f; "HARD" -> 3f else -> 4f }
                val distanceScore = (distance / 20f).toFloat().coerceIn(0f, 1f)
                val elevScore = (elevationGain / 2000f).coerceIn(0f, 1f)
                val popScore = (popularity / 100f).coerceIn(0f, 1f)
                val ratingScore = (rating / 5f).toFloat()
                val score = (diffNumeric * 0.25f + distanceScore * 0.15f + elevScore * 0.15f + popScore * 0.2f + ratingScore * 0.25f)

                val reasons = mutableListOf<String>()
                if (ratingScore > 0.8f) reasons.add("Highly rated by hikers")
                if (popScore > 0.7f) reasons.add("Popular among adventurers")
                if (diffNumeric <= 2f) reasons.add("Beginner-friendly trail")
                if (distanceScore > 0.5f) reasons.add("Full day hike")
                if (reasons.isEmpty()) reasons.add("Well-balanced trail")

                RouteRecommendation(
                    trailId = doc.id, trailName = name, mountainName = mountainName,
                    score = score, reasons = reasons,
                    estimatedDurationMinutes = duration, difficultyMatch = diffNumeric,
                    seasonMatch = true
                )
            }.sortedByDescending { it.score }.take(10)

            trySend(DataResult.Success(recommendations))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }

    override suspend fun getTrailAnalysis(trailId: String): DataResult<TrailAnalysis> {
        return try {
            val doc = firestoreManager.getCollection("trails").document(trailId).get().await()
            if (!doc.exists()) return DataResult.Error(Exception("Trail not found"))

            val reviewsSnap = firestoreManager.getCollection("trail_reviews")
                .whereEqualTo("trailId", trailId).get().await()

            val avgRating = reviewsSnap.documents.mapNotNull { it.getDouble("rating") }
                .let { if (it.isEmpty()) 3.5f else (it.sum() / it.size).toFloat() }

            val popularity = doc.getLong("popularity")?.toInt() ?: 0
            val crowdLevel = when {
                popularity > 80 -> CrowdLevel.PEAK
                popularity > 50 -> CrowdLevel.BUSY
                popularity > 20 -> CrowdLevel.MODERATE
                popularity > 5 -> CrowdLevel.LIGHT
                else -> CrowdLevel.EMPTY
            }

            DataResult.Success(TrailAnalysis(
                trailId = trailId,
                avgDurationMinutes = doc.getLong("durationMinutes")?.toDouble() ?: 0.0,
                successRate = reviewsSnap.documents.mapNotNull { it.getDouble("rating") }
                    .let { ratings ->
                        if (ratings.isEmpty()) 0.75f
                        else ratings.count { it >= 4.0 }.toFloat() / ratings.size
                    },
                avgRating = avgRating,
                bestSeason = doc.getString("bestSeason"),
                crowdLevel = crowdLevel
            ))
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun getSafetyAlerts(
        trailId: String, currentHour: Int, distanceKm: Double, elevation: Int
    ): List<SafetyAlert> {
        val alerts = mutableListOf<SafetyAlert>()

        val sunsetHour = 18
        if (currentHour >= sunsetHour - 2) {
            alerts.add(SafetyAlert(
                SafetyAlertType.DARKNESS_UPCOMING, SafetySeverity.WARNING,
                "Sunset approaching in ${sunsetHour - currentHour}h",
                "Ensure you have a headlamp and warm layers. Consider turning back if far from base.",
                trailId
            ))
        }
        if (currentHour >= sunsetHour) {
            alerts.add(SafetyAlert(
                SafetyAlertType.DARKNESS_UPCOMING, SafetySeverity.CRITICAL,
                "It's dark now! Hiking in darkness is dangerous.",
                "Use your headlamp and proceed with extreme caution. Inform base camp.",
                trailId
            ))
        }

        if (distanceKm > 15.0) {
            alerts.add(SafetyAlert(
                SafetyAlertType.DISTANCE_ALERT, SafetySeverity.WARNING,
                "Long distance hike (${"%.1f".format(distanceKm)}km)",
                "Carry at least 2L water and high-energy snacks. Start early.",
                trailId
            ))
        }

        if (elevation > 2500) {
            alerts.add(SafetyAlert(
                SafetyAlertType.ELEVATION_GAIN_ALERT, SafetySeverity.INFO,
                "High altitude (${elevation}m) - watch for AMS symptoms",
                "Ascend slowly, stay hydrated. Descend if dizzy or nauseous.",
                trailId
            ))
        }
        if (elevation > 3500) {
            alerts.add(SafetyAlert(
                SafetyAlertType.ELEVATION_GAIN_ALERT, SafetySeverity.WARNING,
                "Very high altitude (${elevation}m) - high AMS risk",
                "Consider diamox, mandatory acclimatization day recommended.",
                trailId
            ))
        }

        return alerts
    }

    override suspend fun getWeatherForecast(latitude: Double, longitude: Double): DataResult<WeatherForecast> {
        return try {
            if (!isWeatherKeyReal) {
                return fallbackForecast()
            }
            val response = weatherApi.getCurrentWeather(latitude, longitude, BuildConfig.WEATHER_API_KEY)

            val now = Calendar.getInstance()
            val forecast = (0..5).map { i ->
                val h = (now.get(Calendar.HOUR_OF_DAY) + i * 3) % 24
                HourlyForecast(
                    hour = h,
                    temperature = response.main.temp,
                    condition = response.weather.firstOrNull()?.description ?: "Cloudy",
                    rainChance = if (response.weather.firstOrNull()?.description?.contains("rain", ignoreCase = true) == true) 0.6f else 0.2f
                )
            }

            DataResult.Success(WeatherForecast(
                temperature = response.main.temp,
                feelsLike = response.main.temp - 2f,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                condition = response.weather.firstOrNull()?.description ?: "Cloudy",
                rainChance = if (response.weather.firstOrNull()?.description?.contains("rain", ignoreCase = true) == true) 0.6f else 0.2f,
                hourForecast = forecast
            ))
        } catch (e: Exception) {
            fallbackForecast()
        }
    }

    /** Deterministic fallback — no Math.random() */
    private fun fallbackForecast(): DataResult<WeatherForecast> {
        val now = Calendar.getInstance()
        val forecast = (0..5).map { i ->
            val h = (now.get(Calendar.HOUR_OF_DAY) + i * 3) % 24
            HourlyForecast(hour = h, temperature = 24f, condition = "Cloudy", rainChance = 0.3f)
        }
        return DataResult.Success(WeatherForecast(
            temperature = 24f, feelsLike = 22f, humidity = 80, windSpeed = 5f,
            condition = "Cloudy", rainChance = 0.3f, hourForecast = forecast
        ))
    }
}
