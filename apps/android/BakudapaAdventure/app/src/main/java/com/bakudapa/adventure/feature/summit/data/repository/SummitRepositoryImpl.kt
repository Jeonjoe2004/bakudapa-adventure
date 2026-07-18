package com.bakudapa.adventure.feature.summit.data.repository

import android.content.Context
import android.location.Location
import android.net.Uri
import androidx.work.*
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.core.network.NetworkMonitor
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.data.remote.firebase.StorageManager
import com.bakudapa.adventure.feature.summit.data.local.PendingSummitLog
import com.bakudapa.adventure.feature.summit.data.local.SummitLogDatabase
import com.bakudapa.adventure.feature.summit.data.local.SummitSyncWorker
import com.bakudapa.adventure.feature.summit.domain.model.SummitLog
import com.bakudapa.adventure.feature.summit.domain.repository.SummitRepository
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

private const val SUMMIT_RADIUS_METERS = 1000.0

@Singleton
class SummitRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val storageManager: StorageManager,
    private val networkMonitor: NetworkMonitor
) : SummitRepository {

    private val db by lazy { SummitLogDatabase.getInstance(context) }
    private val dao by lazy { db.summitLogDao() }

    override fun getSummitLogs(mountainId: String): Flow<DataResult<List<SummitLog>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("summit_logs")
            .whereEqualTo("mountainId", mountainId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val logs = snap?.documents?.mapNotNull { it.toObject(SummitLog::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(logs))
            }
        awaitClose { listener.remove() }
    }

    /** Cek jarak user vs puncak gunung */
    override suspend fun checkInRadius(mountainId: String, latitude: Double, longitude: Double): Boolean {
        return try {
            val doc = firestoreManager.getCollection("mountains").document(mountainId).get().await()
            val mountainLat = doc.getDouble("latitude") ?: return true // ga ada koordinat → skip validasi
            val mountainLng = doc.getDouble("longitude") ?: return true
            val distance = haversine(latitude, longitude, mountainLat, mountainLng)
            distance <= SUMMIT_RADIUS_METERS
        } catch (_: Exception) { true }
    }

    override suspend fun createSummitLog(mountainId: String, mountainName: String, caption: String, photoUri: Uri?): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")

            // Upload foto dulu (butuh internet)
            var photoUrl: String? = null
            if (photoUri != null) {
                val filename = "summit_${UUID.randomUUID()}"
                val ref = storageManager.getReference("summit_logs/$filename")
                ref.putFile(photoUri).await()
                photoUrl = ref.downloadUrl.await().toString()
            }

            val isOnline = networkMonitor.isOnline.first()

            if (isOnline) {
                // Kirim langsung ke callable function
                val functions = com.google.firebase.functions.FirebaseFunctions.getInstance()
                val data = mapOf(
                    "mountainId" to mountainId,
                    "mountainName" to mountainName,
                    "caption" to caption,
                    "photoUrl" to photoUrl
                )
                functions.getHttpsCallable("createSummitLog").call(data).await()
            } else {
                // Simpan ke Room queue
                dao.insert(PendingSummitLog(
                    mountainId = mountainId,
                    mountainName = mountainName,
                    caption = caption,
                    photoUrl = photoUrl
                ))
                // Jadwalkan sync
                scheduleSync()
            }

            DataResult.Success(Unit)
        } catch (e: Exception) {
            val msg = when {
                e.message?.contains("already-exists") == true -> "Kamu sudah check-in hari ini untuk gunung ini"
                e.message?.contains("terlalu jauh") == true -> e.message ?: "Lokasi terlalu jauh dari puncak"
                else -> e.message ?: "Gagal menyimpan summit log"
            }
            DataResult.Error(Exception(msg))
        }
    }

    override suspend fun getPendingCount(): Int = dao.count()

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SummitSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    companion object {
        fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2).pow(2)
            return R * 2 * atan2(sqrt(a), sqrt(1 - a))
        }
    }
}
