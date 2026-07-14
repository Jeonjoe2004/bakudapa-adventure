package com.bakudapa.adventure.feature.trail.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail
import com.bakudapa.adventure.feature.trail.domain.repository.TrailRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrailRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager
) : TrailRepository {

    override fun getTrailDetail(trailId: String): Flow<DataResult<TrailDetail>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val doc = firestoreManager.getCollection("trails").document(trailId).get().await()
            val trail = TrailDetail(
                id = doc.id,
                name = doc.getString("name") ?: "",
                mountainId = doc.getString("mountainId") ?: "",
                mountainName = doc.getString("mountainName") ?: "",
                difficulty = doc.getString("difficulty") ?: "MODERATE",
                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                imageUrl = doc.getString("imageUrl") ?: "",
                description = doc.getString("description") ?: "",
                popularity = doc.getLong("popularity")?.toInt() ?: 0,
                elevationGain = doc.getLong("elevationGain")?.toInt() ?: 0,
                maxElevation = doc.getLong("maxElevation")?.toInt() ?: 0,
                recommendedGear = doc.get("recommendedGear") as? List<String> ?: emptyList(),
                waterSources = doc.get("waterSources") as? List<String> ?: emptyList(),
                campingSpots = doc.get("campingSpots") as? List<String> ?: emptyList(),
            )
            trySend(DataResult.Success(trail))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }
}
