package com.bakudapa.adventure.feature.map.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import com.bakudapa.adventure.feature.map.domain.model.MarkerType
import com.bakudapa.adventure.feature.map.domain.repository.MapRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.maplibre.android.MapLibre
import org.maplibre.android.offline.OfflineManager
import org.maplibre.android.offline.OfflineRegion
import org.maplibre.android.offline.OfflineRegionError
import org.maplibre.android.offline.OfflineRegionStatus
import org.maplibre.android.offline.OfflineTilePyramidRegionDefinition
import org.maplibre.android.storage.FileSource
import javax.inject.Inject
import javax.inject.Singleton

private val Context.offlineStore: DataStore<Preferences> by preferencesDataStore(name = "offline_maps")

@Singleton
class MapRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val context: Context
) : MapRepository {

    private val downloadedKey = stringSetPreferencesKey("downloaded_regions")
    private val offlineManager by lazy { OfflineManager.getInstance(context) }

    override fun getMapMarkers(): Flow<DataResult<List<MapMarker>>> = callbackFlow {
        trySend(DataResult.Loading)

        try {
            val markers = mutableListOf<MapMarker>()

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
            val styleUrl = "https://demotiles.maplibre.org/style.json"
            val bounds = listOf(124.0, 1.0, 125.0, 2.0) // west, south, east, north
            val definition = OfflineTilePyramidRegionDefinition(
                styleUrl, bounds[0], bounds[1], bounds[2], bounds[3],
                0f, 14f, context.resources.displayMetrics.density
            )

            // Gunakan suspendCoroutine untuk callback-style API
            kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->
                offlineManager.createOfflineRegion(definition, regionName) { region ->
                    region.setDownloadState(OfflineRegion.STATE_ACTIVE)
                    region.setObserver(object : OfflineRegion.OfflineRegionObserver {
                        override fun onStatusChanged(status: OfflineRegionStatus) {
                            if (status.isComplete) {
                                region.setDownloadState(OfflineRegion.STATE_INACTIVE)
                                cont.resume(Unit) {}
                            }
                        }
                        override fun onError(error: OfflineRegionError) {
                            if (!cont.isCompleted) cont.resumeWithException(Exception(error.message))
                        }
                        override fun mapLoadingSucceded() {}
                    })
                    region.setDownloadState(OfflineRegion.STATE_ACTIVE)
                }
            }

            // Simpan ke DataStore sebagai persistent state
            context.offlineStore.edit { prefs ->
                val current = prefs[downloadedKey] ?: emptySet()
                prefs[downloadedKey] = current + regionName
            }

            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    /** Daftar region yang sudah di-download */
    fun getDownloadedRegions(): Flow<Set<String>> =
        context.offlineStore.data.map { it[downloadedKey] ?: emptySet() }

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
