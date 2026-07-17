package com.bakudapa.adventure.feature.profile.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.FollowUser
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.profile.domain.model.UserStats
import com.bakudapa.adventure.feature.profile.domain.repository.ProfileRepository
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val hikingRouteDao: HikingRouteDao
) : ProfileRepository {

    private val ioScope = CoroutineScope(Dispatchers.IO)

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
                val level = snapshot?.getLong("level")?.toInt() ?: 1
                val xp = snapshot?.getLong("xp")?.toInt() ?: 0
                val username = snapshot?.getString("username") ?: ""
                val bio = snapshot?.getString("bio") ?: ""
                val website = snapshot?.getString("website") ?: ""
                ioScope.launch {
                    val routes = hikingRouteDao.getAllRoutes().firstOrNull().orEmpty()
                    val totalDist = routes.sumOf { it.distanceMeters / 1000.0 }
                    val totalElev = routes.maxOfOrNull { it.maxElevation.toInt() } ?: 0
                    val climbs = routes.size
                    val totalHrs = routes.sumOf { (it.durationMillis / 3600000.0) }
                    val profile = UserProfile(
                        id = userId, name = name, email = email,
                        photoUrl = photoUrl, level = level, xp = xp,
                        stats = UserStats(totalDist, totalElev, climbs, totalHrs),
                        username = username, bio = bio, website = website
                    )
                    trySend(DataResult.Success(profile))
                }
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

    override suspend fun updateProfile(name: String, username: String, bio: String, website: String, photoUrl: String?): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val updates = mapOf(
                "displayName" to name,
                "username" to username,
                "bio" to bio,
                "website" to website,
                "photoUrl" to photoUrl
            )
            firestoreManager.getCollection("users").document(user.uid).update(updates).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun followUser(targetUserId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val follow = mapOf("followedAt" to System.currentTimeMillis())
            firestoreManager.getCollection("users").document(targetUserId)
                .collection("followers").document(userId).set(follow).await()
            firestoreManager.getCollection("users").document(userId)
                .collection("following").document(targetUserId).set(follow).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun unfollowUser(targetUserId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            firestoreManager.getCollection("users").document(targetUserId)
                .collection("followers").document(userId).delete().await()
            firestoreManager.getCollection("users").document(userId)
                .collection("following").document(targetUserId).delete().await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun isFollowing(targetUserId: String): Flow<DataResult<Boolean>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestoreManager.getCollection("users").document(targetUserId)
            .collection("followers").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                trySend(DataResult.Success(snapshot?.exists() == true))
            }
        awaitClose { listener.remove() }
    }

    override fun getFollowers(userId: String): Flow<DataResult<List<FollowUser>>> = callbackFlow {
        val ref = firestoreManager.getCollection("users").document(userId).collection("followers")
        val listener = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                val users = snapshot.documents.mapNotNull { doc ->
                    try {
                        val userDoc = firestoreManager.getCollection("users").document(doc.id).get().await()
                        FollowUser(id = doc.id, name = userDoc.getString("displayName") ?: "Adventurer", photoUrl = userDoc.getString("photoUrl"))
                    } catch (_: Exception) { null }
                }
                trySend(DataResult.Success(users))
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getFollowing(userId: String): Flow<DataResult<List<FollowUser>>> = callbackFlow {
        val ref = firestoreManager.getCollection("users").document(userId).collection("following")
        val listener = ref.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                val users = snapshot.documents.mapNotNull { doc ->
                    try {
                        val userDoc = firestoreManager.getCollection("users").document(doc.id).get().await()
                        FollowUser(id = doc.id, name = userDoc.getString("displayName") ?: "Adventurer", photoUrl = userDoc.getString("photoUrl"))
                    } catch (_: Exception) { null }
                }
                trySend(DataResult.Success(users))
            }
        }
        awaitClose { listener.remove() }
    }

    override fun getFollowersCount(userId: String): Flow<DataResult<Int>> = callbackFlow {
        val listener = firestoreManager.getCollection("users").document(userId)
            .collection("followers")
            .addSnapshotListener { snapshot, _ ->
                trySend(DataResult.Success(snapshot?.documents?.size ?: 0))
            }
        awaitClose { listener.remove() }
    }

    override fun getFollowingCount(userId: String): Flow<DataResult<Int>> = callbackFlow {
        val listener = firestoreManager.getCollection("users").document(userId)
            .collection("following")
            .addSnapshotListener { snapshot, _ ->
                trySend(DataResult.Success(snapshot?.documents?.size ?: 0))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun searchUsers(query: String): DataResult<List<FollowUser>> {
        return try {
            val snap = firestoreManager.getCollection("users")
                .orderBy("displayName")
                .startAt(query)
                .endAt(query + "")
                .limit(20)
                .get()
                .await()
            val users = snap.documents.mapNotNull { doc ->
                FollowUser(
                    id = doc.id,
                    name = doc.getString("displayName") ?: "Adventurer",
                    photoUrl = doc.getString("photoUrl")
                )
            }
            DataResult.Success(users)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    private fun com.bakudapa.adventure.feature.tracking.data.local.HikingRouteEntity.toDomain() = HikingRoute(
        id = id, name = name, distanceMeters = distanceMeters, durationMillis = durationMillis,
        avgSpeed = avgSpeed, maxElevation = maxElevation, minElevation = minElevation,
        calories = calories, startTime = startTime, endTime = endTime
    )
}
