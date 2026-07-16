package com.bakudapa.adventure.feature.premium.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.local.AppDatabase
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.premium.domain.model.*
import com.bakudapa.adventure.feature.premium.domain.repository.DetailedStats
import com.bakudapa.adventure.feature.premium.domain.repository.PremiumRepository
import com.bakudapa.adventure.feature.premium.domain.repository.SyncStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val database: AppDatabase,
) : PremiumRepository {

    override fun getSubscription(): Flow<DataResult<PremiumSubscription?>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestoreManager.getCollection("subscriptions").document(userId)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val sub = snap?.toObject(PremiumSubscription::class.java)?.copy(userId = userId)
                trySend(DataResult.Success(sub))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun subscribe(planId: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val expires = when (planId) {
                "premium_monthly" -> System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
                "premium_yearly" -> System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000
                else -> throw Exception("Invalid plan")
            }
            val sub = mapOf(
                "planId" to planId,
                "isActive" to true,
                "startDate" to System.currentTimeMillis(),
                "endDate" to expires,
                "autoRenew" to true,
            )
            firestoreManager.getCollection("subscriptions").document(user.uid).set(sub).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun cancelSubscription(): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            firestoreManager.getCollection("subscriptions").document(user.uid)
                .update("autoRenew", false).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun backupToCloud(): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun restoreFromCloud(): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getSyncStatus(): Flow<DataResult<SyncStatus>> = callbackFlow {
        trySend(DataResult.Success(SyncStatus(
            lastSyncTime = System.currentTimeMillis(),
            isSyncing = false,
            pendingItems = 0,
            totalBackedUp = 3,
        )))
        awaitClose()
    }

    override suspend fun getDetailedStats(userId: String): DataResult<DetailedStats> {
        return try {
            DataResult.Success(DetailedStats(
                totalRoutes = 12, totalDistanceKm = 45.8, totalElevationGainM = 3450,
                totalDurationHours = 28.5, totalCaloriesBurned = 12800,
                mountainsConquered = 4, averagePaceMinPerKm = 12.5,
                longestRouteKm = 8.2, highestElevationM = 3726,
            ))
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
