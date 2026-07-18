package com.bakudapa.adventure.feature.mountain.data.repository

import com.bakudapa.adventure.BuildConfig
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.home.data.remote.WeatherApi
import com.bakudapa.adventure.feature.home.domain.model.Weather
import com.bakudapa.adventure.feature.mountain.domain.model.*
import com.bakudapa.adventure.feature.mountain.domain.repository.MountainRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MountainRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val weatherApi: WeatherApi
) : MountainRepository {

    override fun getMountains(): Flow<DataResult<List<Mountain>>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val snap = firestoreManager.getCollection("mountains")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val mountains = snap.documents.mapNotNull { doc ->
                Mountain(
                    id = doc.id,
                    name = doc.getString("name") ?: return@mapNotNull null,
                    location = doc.getString("location") ?: "",
                    elevation = doc.getLong("elevation")?.toInt() ?: 0,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    rating = doc.getDouble("rating")?.toFloat() ?: 0f,
                    difficulty = MountainDifficulty.valueOf(doc.getString("difficulty") ?: "MODERATE"),
                    distance = doc.getDouble("distance"),
                )
            }
            trySend(DataResult.Success(mountains))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }

    override fun getMountainDetail(mountainId: String): Flow<DataResult<MountainDetail>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val doc = firestoreManager.getCollection("mountains").document(mountainId).get().await()
            val detail = MountainDetail(
                id = doc.id,
                name = doc.getString("name") ?: "",
                location = doc.getString("location") ?: "",
                elevation = doc.getLong("elevation")?.toInt() ?: 0,
                imageUrl = doc.getString("imageUrl") ?: "",
                rating = doc.getDouble("rating")?.toFloat() ?: 0f,
                description = doc.getString("description") ?: "",
                difficulty = MountainDifficulty.valueOf(doc.getString("difficulty") ?: "MODERATE"),
                bestSeason = doc.getString("bestSeason") ?: "",
                latitude = doc.getDouble("latitude") ?: 0.0,
                longitude = doc.getDouble("longitude") ?: 0.0,
                distance = doc.getDouble("distance"),
                weatherTip = doc.getString("weatherTip") ?: "",
            )
            trySend(DataResult.Success(detail))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }

    override fun getTrails(mountainId: String): Flow<DataResult<List<TrailInfo>>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val snap = firestoreManager.getCollection("trails")
                .whereEqualTo("mountainId", mountainId)
                .orderBy("popularity", Query.Direction.DESCENDING)
                .get()
                .await()

            val trails = snap.documents.mapNotNull { doc ->
                TrailInfo(
                    id = doc.id,
                    name = doc.getString("name") ?: return@mapNotNull null,
                    mountainName = doc.getString("mountainName") ?: "",
                    difficulty = MountainDifficulty.valueOf(doc.getString("difficulty") ?: "MODERATE"),
                    durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                    distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    description = doc.getString("description") ?: "",
                )
            }
            trySend(DataResult.Success(trails))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }

    override fun getWeather(lat: Double, lon: Double): Flow<DataResult<Weather>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val key = BuildConfig.WEATHER_API_KEY
            if (key.isBlank() || key == "CHANGE_ME") {
                trySend(DataResult.Success(Weather(24f, "Cloudy", "", 80, 5f)))
            } else {
                val response = weatherApi.getCurrentWeather(lat, lon, key)
                val weather = Weather(
                    temperature = response.main.temp,
                    condition = response.weather.firstOrNull()?.description ?: "Unknown",
                    iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png",
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed
                )
                trySend(DataResult.Success(weather))
            }
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }
}
