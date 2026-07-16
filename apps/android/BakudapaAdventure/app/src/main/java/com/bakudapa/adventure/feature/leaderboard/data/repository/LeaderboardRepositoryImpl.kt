package com.bakudapa.adventure.feature.leaderboard.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardEntry
import com.bakudapa.adventure.feature.leaderboard.domain.repository.LeaderboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val firestoreManager: FirestoreManager
) : LeaderboardRepository {

    override fun getLeaderboard(): Flow<DataResult<List<LeaderboardEntry>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val entries = snapshot?.documents?.mapNotNull { doc ->
                    val stats = doc.get("stats") as? Map<*, *>
                    val totalDist = (stats?.get("totalDistanceKm") as? Number)?.toDouble() ?: 0.0
                    val totalElev = (stats?.get("totalElevationM") as? Number)?.toInt() ?: 0
                    val climbs = (stats?.get("mountainsClimbed") as? Number)?.toInt() ?: 0

                    LeaderboardEntry(
                        userId = doc.id,
                        displayName = doc.getString("displayName") ?: "Adventurer",
                        photoUrl = doc.getString("photoUrl"),
                        totalDistanceKm = totalDist,
                        totalElevationM = totalElev,
                        mountainsClimbed = climbs,
                        badgesEarned = (doc.get("badgeCount") as? Number)?.toInt() ?: 0
                    )
                }?.sortedByDescending { it.mountainsClimbed }?.mapIndexed { i, e ->
                    e.copy(rank = i + 1)
                } ?: emptyList()
                trySend(DataResult.Success(entries))
            }
        awaitClose { listener.remove() }
    }
}
