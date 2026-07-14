package com.bakudapa.adventure.feature.badge.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.badge.domain.model.Badge
import com.bakudapa.adventure.feature.badge.domain.repository.BadgeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : BadgeRepository {

    override fun getMyBadges(): Flow<DataResult<List<Badge>>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestoreManager.getCollection("users").document(userId).collection("badges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val badges = snapshot?.documents?.mapNotNull { it.toObject(Badge::class.java) } ?: emptyList()
                trySend(DataResult.Success(badges))
            }
        awaitClose { listener.remove() }
    }

    override fun getAllBadges(): Flow<DataResult<List<Badge>>> = callbackFlow {
        val listener = firestoreManager.getCollection("badges")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val badges = snapshot?.documents?.mapNotNull { it.toObject(Badge::class.java) } ?: emptyList()
                trySend(DataResult.Success(badges))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun unlockBadge(badgeId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val badge = mapOf(
                "badgeId" to badgeId,
                "unlockedAt" to System.currentTimeMillis(),
                "isUnlocked" to true
            )
            firestoreManager.getCollection("users").document(userId).collection("badges").document(badgeId).set(badge).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
