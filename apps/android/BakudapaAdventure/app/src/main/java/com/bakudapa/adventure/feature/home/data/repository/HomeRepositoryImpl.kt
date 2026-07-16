package com.bakudapa.adventure.feature.home.data.repository

import com.bakudapa.adventure.BuildConfig
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.home.data.remote.WeatherApi
import com.bakudapa.adventure.feature.home.domain.model.*
import com.bakudapa.adventure.feature.home.domain.repository.HomeRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val weatherApi: WeatherApi
) : HomeRepository {

    private val defaultLat = 1.4748
    private val defaultLon = 124.8428

    // Ganti WEATHER_API_KEY di local.properties untuk pakai data cuaca real
    private val weatherApiKey = BuildConfig.WEATHER_API_KEY
    private val isWeatherKeyReal: Boolean
        get() = weatherApiKey.isNotBlank() && weatherApiKey != "CHANGE_ME"

    override fun getHomeData(): Flow<DataResult<HomeData>> = callbackFlow {
        trySend(DataResult.Loading)

        try {
            // Fetch recommended mountains (top rated)
            val recommendedSnapshot = firestoreManager.getCollection("mountains")
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val recommendedMountains = recommendedSnapshot.documents.mapNotNull { doc ->
                doc.toMountain()
            }

            // Nearby mountains: filter by distance from default location
            val allSnapshot = firestoreManager.getCollection("mountains")
                .limit(50)
                .get()
                .await()

            val nearbyMountains = allSnapshot.documents.mapNotNull { doc ->
                val lat = doc.getDouble("latitude")
                val lon = doc.getDouble("longitude")
                if (lat != null && lon != null) {
                    doc.toMountain()?.copy(
                        distance = haversine(defaultLat, defaultLon, lat, lon)
                    )
                } else null
            }.sortedBy { it.distance }.take(3)

            // Fetch popular trails
            val trailsSnapshot = firestoreManager.getCollection("trails")
                .orderBy("popularity", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val popularTrails = trailsSnapshot.documents.mapNotNull { doc ->
                doc.toTrail()
            }

            // Fetch latest posts from feed
            val postsSnapshot = firestoreManager.getCollection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()

            val latestPosts = postsSnapshot.documents.mapNotNull { doc ->
                doc.toPost()
            }

            // Weather — skip network call if API key not configured
            val weather = if (isWeatherKeyReal) {
                try {
                    val response = weatherApi.getCurrentWeather(defaultLat, defaultLon, weatherApiKey)
                    Weather(
                        temperature = response.main.temp,
                        condition = response.weather.firstOrNull()?.description ?: "Unknown",
                        iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png",
                        humidity = response.main.humidity,
                        windSpeed = response.wind.speed
                    )
                } catch (e: Exception) {
                    Weather(24f, "Cloudy", "", 80, 5f)
                }
            } else {
                Weather(24f, "Cloudy", "", 80, 5f)
            }

            val homeData = HomeData(
                recommendedMountains = recommendedMountains,
                popularTrails = popularTrails,
                latestPosts = latestPosts,
                nearbyMountains = nearbyMountains,
                weather = weather
            )

            trySend(DataResult.Success(homeData))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }

        awaitClose()
    }

    override suspend fun searchMountains(query: String): DataResult<List<Mountain>> {
        return try {
            val snapshot = firestoreManager.getCollection("mountains")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "")
                .get()
                .await()

            val mountains = snapshot.documents.mapNotNull { it.toMountain() }
            DataResult.Success(mountains)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    /** Haversine distance in km */
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMountain(): Mountain? {
        return try {
            Mountain(
                id = id,
                name = getString("name") ?: return null,
                location = getString("location") ?: "",
                elevation = getLong("elevation")?.toInt() ?: 0,
                imageUrl = getString("imageUrl") ?: "",
                rating = getDouble("rating")?.toFloat() ?: 0f,
                distance = getDouble("distance")
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toTrail(): Trail? {
        return try {
            Trail(
                id = id,
                name = getString("name") ?: return null,
                mountainName = getString("mountainName") ?: "",
                difficulty = TrailDifficulty.valueOf(
                    getString("difficulty") ?: "MODERATE"
                ),
                durationMinutes = getLong("durationMinutes")?.toInt() ?: 0,
                distanceKm = getDouble("distanceKm") ?: 0.0,
                imageUrl = getString("imageUrl") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toPost(): Post? {
        return try {
            Post(
                id = id,
                authorName = getString("authorName") ?: return null,
                authorImageUrl = getString("authorImageUrl") ?: "",
                content = getString("content") ?: "",
                imageUrl = getString("imageUrl"),
                timestamp = getLong("timestamp") ?: System.currentTimeMillis(),
                likesCount = getLong("likesCount")?.toInt() ?: 0,
                commentsCount = getLong("commentsCount")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }
}
