package com.bakudapa.adventure.feature.profile.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.profile.domain.model.UserStats
import com.bakudapa.adventure.feature.profile.domain.repository.ProfileRepository
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val hikingRouteDao: HikingRouteDao
) : ProfileRepository {

    override fun getUserProfile(userId: String): Flow<DataResult<UserProfile>> = callbackFlow {
        val listener = firestoreManager.getCollection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                
                val name = snapshot?.getString("displayName") ?: "Adventurer"
                val photoUrl = snapshot?.getString("photoUrl")
                val email = snapshot?.getString("email") ?: ""
                
                // Mock stats for UI
                val profile = UserProfile(
                    id = userId,
                    name = name,
                    email = email,
                    photoUrl = photoUrl,
                    stats = UserStats(12.5, 1995, 2, 4.5)
                )
                trySend(DataResult.Success(profile))
            }
        awaitClose { listener.remove() }
    }

    override fun getMyPosts(userId: String): Flow<DataResult<List<Post>>> = callbackFlow {
        val listener = firestoreManager.getCollection("posts")
            .whereEqualTo("authorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
                trySend(DataResult.Success(posts))
            }
        awaitClose { listener.remove() }
    }

    override fun getMyRoutes(userId: String): Flow<DataResult<List<HikingRoute>>> = 
        hikingRouteDao.getAllRoutes().map { entities ->
            DataResult.Success(entities.map { it.toDomain() })
        }

    override suspend fun updateProfile(name: String, photoUrl: String?): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val updates = mapOf(
                "displayName" to name,
                "photoUrl" to photoUrl
            )
            firestoreManager.getCollection("users").document(user.uid).update(updates).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    private fun com.bakudapa.adventure.feature.tracking.data.local.HikingRouteEntity.toDomain() = HikingRoute(
        id = id,
        name = name,
        distanceMeters = distanceMeters,
        durationMillis = durationMillis,
        avgSpeed = avgSpeed,
        maxElevation = maxElevation,
        minElevation = minElevation,
        calories = calories,
        startTime = startTime,
        endTime = endTime
    )
}
