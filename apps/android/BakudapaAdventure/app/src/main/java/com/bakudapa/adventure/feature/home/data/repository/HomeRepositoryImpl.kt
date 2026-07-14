package com.bakudapa.adventure.feature.home.data.repository

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

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val weatherApi: WeatherApi
) : HomeRepository {

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
            
            // Weather from real API
            val weather = try {
                // Example: Manado location
                val response = weatherApi.getCurrentWeather(1.4748, 124.8428, "b8a8b1f5f2f5f2f5f2f5f2f5f2f5f2f5") // Dummy key example
                Weather(
                    temperature = response.main.temp,
                    condition = response.weather.firstOrNull()?.description ?: "Unknown",
                    iconUrl = "https://openweathermap.org/img/wn/${response.weather.firstOrNull()?.icon}@2x.png",
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed
                )
            } catch (e: Exception) {
                Weather(24f, "Cloudy", "", 80, 5f) // Fallback
            }
            
            val homeData = HomeData(
                recommendedMountains = recommendedMountains,
                popularTrails = popularTrails,
                latestPosts = latestPosts,
                nearbyMountains = recommendedMountains.take(3), // Placeholder logic
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
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val mountains = snapshot.documents.mapNotNull { it.toMountain() }
            DataResult.Success(mountains)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
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
