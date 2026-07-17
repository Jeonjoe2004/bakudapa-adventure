package com.bakudapa.adventure.feature.story.data.repository

import android.net.Uri
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.data.remote.firebase.StorageManager
import com.bakudapa.adventure.feature.story.domain.model.Story
import com.bakudapa.adventure.feature.story.domain.model.StoryMediaType
import com.bakudapa.adventure.feature.story.domain.model.StoryUser
import com.bakudapa.adventure.feature.story.domain.repository.StoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val storageManager: StorageManager
) : StoryRepository {

    override fun getActiveStories(): Flow<DataResult<List<StoryUser>>> = callbackFlow {
        trySend(DataResult.Loading)
        val userId = auth.currentUser?.uid ?: ""
        val now = System.currentTimeMillis()

        val listener = firestoreManager.getCollection("stories")
            .whereGreaterThan("expiresAt", now)
            .orderBy("expiresAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }

                val allStories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Story::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                // Grup per user
                val userMap = mutableMapOf<String, MutableList<Story>>()
                allStories.forEach { story ->
                    userMap.getOrPut(story.userId) { mutableListOf() }.add(story)
                }

                // Bangun StoryUser list
                val storyUsers = userMap.map { (uid, stories) ->
                    val latest = stories.maxByOrNull { it.createdAt }
                    val hasUnviewed = if (userId.isBlank()) true
                    else stories.any { !it.hasViewed }
                    StoryUser(
                        userId = uid,
                        username = latest?.username ?: "",
                        photoUrl = latest?.userPhotoUrl,
                        latestStory = latest,
                        hasUnviewedStory = hasUnviewed
                    )
                }.sortedByDescending { it.latestStory?.createdAt ?: 0L }

                trySend(DataResult.Success(storyUsers))
            }
        awaitClose { listener.remove() }
    }

    override fun getUserStories(userId: String): Flow<DataResult<List<Story>>> = callbackFlow {
        trySend(DataResult.Loading)
        val now = System.currentTimeMillis()

        val listener = firestoreManager.getCollection("stories")
            .whereEqualTo("userId", userId)
            .whereGreaterThan("expiresAt", now)
            .orderBy("expiresAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val stories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Story::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(DataResult.Success(stories))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createStory(mediaUri: Uri, caption: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")

            // Upload media ke Storage
            val fileName = "story_${UUID.randomUUID()}"
            val ref = storageManager.getReference("stories/$fileName")
            ref.putFile(mediaUri).await()
            val mediaUrl = ref.downloadUrl.await().toString()

            val now = System.currentTimeMillis()
            val story = Story(
                userId = user.uid,
                username = user.displayName ?: "Anonymous",
                userPhotoUrl = user.photoUrl?.toString(),
                mediaUrl = mediaUrl,
                mediaType = StoryMediaType.PHOTO,
                caption = caption,
                createdAt = now,
                expiresAt = now + 86_400_000L
            )

            firestoreManager.getCollection("stories").add(story).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun markViewed(storyId: String): DataResult<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            // Simpan viewer di subcollection + increment counter
            val storyRef = firestoreManager.getCollection("stories").document(storyId)
            firestoreManager.getFirestore().runTransaction { tx ->
                val snap = tx.get(storyRef)
                val current = snap.getLong("viewerCount") ?: 0L
                val viewerRef = storyRef.collection("viewers").document(uid)
                tx.set(viewerRef, mapOf("viewedAt" to System.currentTimeMillis()))
                tx.update(storyRef, "viewerCount", current + 1)
            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun deleteStory(storyId: String): DataResult<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val storyRef = firestoreManager.getCollection("stories").document(storyId)
            val story = storyRef.get().await()
            if (story.getString("userId") != uid) throw Exception("Not your story")
            storyRef.delete().await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
