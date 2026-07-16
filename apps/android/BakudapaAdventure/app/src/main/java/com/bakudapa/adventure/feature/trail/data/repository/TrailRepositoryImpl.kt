package com.bakudapa.adventure.feature.trail.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDifficulty
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import com.bakudapa.adventure.feature.trail.domain.model.Checkpoint
import com.bakudapa.adventure.feature.trail.domain.model.PointOfInterest
import com.bakudapa.adventure.feature.trail.domain.model.PoiType
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail
import com.bakudapa.adventure.feature.trail.domain.repository.TrailRepository
import com.google.firebase.firestore.Query
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
                difficulty = MountainDifficulty.valueOf(doc.getString("difficulty") ?: "MODERATE"),
                durationMinutes = doc.getLong("durationMinutes")?.toInt() ?: 0,
                distanceKm = doc.getDouble("distanceKm") ?: 0.0,
                imageUrl = doc.getString("imageUrl") ?: "",
                description = doc.getString("description") ?: "",
                popularity = doc.getLong("popularity")?.toInt() ?: 0,
                elevationGain = doc.getLong("elevationGain")?.toInt() ?: 0,
                maxElevation = doc.getLong("maxElevation")?.toInt() ?: 0,
                recommendedGear = doc.get("recommendedGear") as? List<String> ?: emptyList(),
                pointsOfInterest = parsePoiList(doc.get("pointsOfInterest")),
                checkpoints = parseCheckpoints(doc.get("checkpoints")),
            )
            trySend(DataResult.Success(trail))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        awaitClose()
    }

    override fun getOtherTrails(mountainId: String, excludeTrailId: String): Flow<DataResult<List<TrailInfo>>> = callbackFlow {
        trySend(DataResult.Loading)
        try {
            val snap = firestoreManager.getCollection("trails")
                .whereEqualTo("mountainId", mountainId)
                .orderBy("popularity", Query.Direction.DESCENDING)
                .get()
                .await()

            val trails = snap.documents.mapNotNull { doc ->
                if (doc.id == excludeTrailId) return@mapNotNull null
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

    @Suppress("UNCHECKED_CAST")
    private fun parseCheckpoints(raw: Any?): List<Checkpoint> {
        if (raw !is List<*>) return emptyList()
        return raw.mapNotNull { item ->
            if (item !is Map<*, *>) return@mapNotNull null
            try {
                Checkpoint(
                    name = item["name"] as? String ?: return@mapNotNull null,
                    elevation = (item["elevation"] as? Number)?.toInt() ?: 0,
                    eta = item["eta"] as? String ?: "",
                )
            } catch (_: Exception) { null }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePoiList(raw: Any?): List<PointOfInterest> {
        if (raw !is List<*>) return emptyList()
        return raw.mapNotNull { item ->
            if (item !is Map<*, *>) return@mapNotNull null
            try {
                PointOfInterest(
                    name = item["name"] as? String ?: return@mapNotNull null,
                    type = try {
                        PoiType.valueOf((item["type"] as? String ?: "TRAIL_HEAD"))
                    } catch (_: IllegalArgumentException) { PoiType.TRAIL_HEAD },
                    latitude = (item["latitude"] as? Number)?.toDouble() ?: 0.0,
                    longitude = (item["longitude"] as? Number)?.toDouble() ?: 0.0,
                    elevation = (item["elevation"] as? Number)?.toInt() ?: 0,
                    description = item["description"] as? String ?: "",
                )
            } catch (_: Exception) { null }
        }
    }
}
