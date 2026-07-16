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
            val markers = mutableListOf<MapMarker>()

            // 1. Fetch mountain markers
            val mountainSnap = firestoreManager.getCollection("mountains").get().await()
            markers.addAll(mountainSnap.documents.mapNotNull { doc ->
                try {
                    val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                    val lng = doc.getDouble("longitude") ?: return@mapNotNull null
                    MapMarker(
                        id = doc.id,
                        title = doc.getString("name") ?: return@mapNotNull null,
                        description = doc.getString("description") ?: "",
                        latitude = lat, longitude = lng,
                        type = MarkerType.MOUNTAIN,
                        elevation = doc.getLong("elevation")?.toInt()
                    )
                } catch (_: Exception) { null }
            })

            // 2. Fetch POI from trails collection
            val trailSnap = firestoreManager.getCollection("trails").get().await()
            markers.addAll(trailSnap.documents.flatMap { trailDoc ->
                val pois = trailDoc.get("pointsOfInterest")
                if (pois !is List<*>) return@flatMap emptyList()
                pois.mapNotNull { item ->
                    if (item !is Map<*, *>) return@mapNotNull null
                    try {
                        val lat = (item["latitude"] as? Number)?.toDouble() ?: return@mapNotNull null
                        val lng = (item["longitude"] as? Number)?.toDouble() ?: return@mapNotNull null
                        val typeStr = (item["type"] as? String) ?: "TRAIL_HEAD"
                        MapMarker(
                            id = "${trailDoc.id}_${item["name"]}_${typeStr}",
                            title = item["name"] as? String ?: "POI",
                            description = item["description"] as? String ?: "",
                            latitude = lat, longitude = lng,
                            type = poiTypeToMarkerType(typeStr),
                            elevation = (item["elevation"] as? Number)?.toInt()
                        )
                    } catch (_: Exception) { null }
                }
            })

            trySend(DataResult.Success(markers))
        } catch (e: Exception) {
            trySend(DataResult.Error(e))
        }

        awaitClose()
    }

    override suspend fun downloadMapRegion(regionName: String): DataResult<Unit> {
        return try {
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    private fun poiTypeToMarkerType(poiType: String): MarkerType {
        return when (poiType.uppercase()) {
            "SUMMIT" -> MarkerType.SUMMIT
            "CAMPING_GROUND" -> MarkerType.CAMPING_GROUND
            "WATER_SOURCE" -> MarkerType.WATER_SOURCE
            "DANGER_ZONE" -> MarkerType.DANGER_ZONE
            "SHELTER" -> MarkerType.TRAIL_HEAD
            else -> MarkerType.TRAIL_HEAD
        }
    }
}
