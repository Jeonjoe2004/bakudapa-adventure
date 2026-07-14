package com.bakudapa.adventure.feature.map.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import com.bakudapa.adventure.feature.map.domain.model.MarkerType
import com.bakudapa.adventure.feature.map.domain.repository.MapRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager
) : MapRepository {

    override fun getMapMarkers(): Flow<DataResult<List<MapMarker>>> = callbackFlow {
        trySend(DataResult.Loading)
        
        try {
            // Fetch mountains from Firestore
            val snapshot = firestoreManager.getCollection("mountains")
                .get()
                .await()
            
            val markers = snapshot.documents.mapNotNull { doc ->
                try {
                    val latitude = doc.getDouble("latitude") ?: return@mapNotNull null
                    val longitude = doc.getDouble("longitude") ?: return@mapNotNull null
                    
                    MapMarker(
                        id = doc.id,
                        title = doc.getString("name") ?: return@mapNotNull null,
                        description = doc.getString("description") ?: "",
                        latitude = latitude,
                        longitude = longitude,
                        type = getMarkerTypeFromDifficulty(doc.getString("difficulty")),
                        elevation = doc.getLong("elevation")?.toInt()
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            trySend(DataResult.Success(markers))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }
        
        awaitClose()
    }

    override suspend fun downloadMapRegion(regionName: String): DataResult<Unit> {
        return try {
            // For offline map download, this would need more complex implementation
            // For now, just return success
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
    
    private fun getMarkerTypeFromDifficulty(difficulty: String?): MarkerType {
        return when (difficulty?.uppercase()) {
            "EASY" -> MarkerType.MOUNTAIN
            "MODERATE" -> MarkerType.TRAIL_HEAD
            "HARD" -> MarkerType.SUMMIT
            "EXPERT" -> MarkerType.DANGER_ZONE
            else -> MarkerType.MOUNTAIN
        }
    }
}
