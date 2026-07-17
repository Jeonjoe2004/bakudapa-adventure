package com.bakudapa.adventure.feature.feed.data.repository

import android.net.Uri
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.data.remote.firebase.StorageManager
import com.bakudapa.adventure.feature.feed.domain.model.Comment
import com.bakudapa.adventure.feature.feed.domain.model.MediaType
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.feed.domain.repository.FeedRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager,
    private val storageManager: StorageManager
) : FeedRepository {

    override fun getPosts(): Flow<DataResult<List<Post>>> = callbackFlow {
        trySend(DataResult.Loading)
        val userId = auth.currentUser?.uid ?: ""

        val listener = firestoreManager.getCollection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }

                val rawPosts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                if (userId.isNotBlank() && rawPosts.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val postsWithMeta = rawPosts.map { post ->
                            var liked = false
                            var saved = false
                            try {
                                val likeSnap = firestoreManager.getCollection("posts").document(post.id)
                                    .collection("likes").document(userId).get().await()
                                liked = likeSnap.exists()
                                val savedSnap = firestoreManager.getCollection("users").document(userId)
                                    .collection("savedPosts").document(post.id).get().await()
                                saved = savedSnap.exists()
                            } catch (_: Exception) {}
                            post.copy(isLiked = liked, isSaved = saved)
                        }
                        trySend(DataResult.Success(postsWithMeta))
                    }
                } else {
                    trySend(DataResult.Success(rawPosts))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createPost(
        content: String,
        mediaUri: Uri?,
        hashtags: List<String>,
        mentions: List<String>
    ): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            var uploadedMediaUrl: String? = null
            var mediaType: MediaType? = null
            
            mediaUri?.let { uri ->
                val fileName = UUID.randomUUID().toString()
                val ref = storageManager.getReference("posts/$fileName")
                ref.putFile(uri).await()
                uploadedMediaUrl = ref.downloadUrl.await().toString()
                mediaType = if (uri.toString().contains("video")) MediaType.VIDEO else MediaType.PHOTO
            }
            
            val post = Post(
                authorId = user.uid,
                authorName = user.displayName ?: "Anonymous",
                authorPhotoUrl = user.photoUrl?.toString(),
                content = content,
                mediaUrl = uploadedMediaUrl,
                mediaType = mediaType,
                hashtags = hashtags,
                mentions = mentions,
                timestamp = System.currentTimeMillis()
            )
            
            firestoreManager.getCollection("posts").add(post).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun likePost(postId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val postRef = firestoreManager.getCollection("posts").document(postId)
            val likeRef = postRef.collection("likes").document(userId)
            // Gunakan batch write: tambah like doc + increment counter
            firestoreManager.getFirestore().runTransaction { transaction ->
                val snap = transaction.get(postRef)
                val current = snap.getLong("likesCount") ?: 0L
                transaction.set(likeRef, mapOf("userId" to userId, "timestamp" to System.currentTimeMillis()))
                transaction.update(postRef, "likesCount", current + 1)
            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun unlikePost(postId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val postRef = firestoreManager.getCollection("posts").document(postId)
            val likeRef = postRef.collection("likes").document(userId)
            firestoreManager.getFirestore().runTransaction { transaction ->
                val snap = transaction.get(postRef)
                val current = snap.getLong("likesCount") ?: 1L
                transaction.delete(likeRef)
                transaction.update(postRef, "likesCount", maxOf(0, current - 1))
            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun savePost(postId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            firestoreManager.getCollection("users").document(userId)
                .collection("savedPosts")
                .document(postId)
                .set(mapOf("postId" to postId, "savedAt" to System.currentTimeMillis()))
                .await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun unsavePost(postId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            firestoreManager.getCollection("users").document(userId)
                .collection("savedPosts")
                .document(postId)
                .delete()
                .await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getComments(postId: String): Flow<DataResult<List<Comment>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("posts").document(postId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { it.toObject(Comment::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(comments))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(postId: String, content: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            val postRef = firestoreManager.getCollection("posts").document(postId)
            val comment = Comment(
                postId = postId,
                authorId = user.uid,
                authorName = user.displayName ?: "Anonymous",
                authorPhotoUrl = user.photoUrl?.toString(),
                content = content,
                timestamp = System.currentTimeMillis()
            )
            firestoreManager.getFirestore().runTransaction { transaction ->
                val snap = transaction.get(postRef)
                val current = snap.getLong("commentsCount") ?: 0L
                val newCommentRef = postRef.collection("comments").document()
                transaction.set(newCommentRef, comment)
                transaction.update(postRef, "commentsCount", current + 1)
            }.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun reportPost(postId: String, reason: String): DataResult<Unit> {
        return try {
            val report = mapOf(
                "postId" to postId,
                "reason" to reason,
                "reporterId" to auth.currentUser?.uid,
                "timestamp" to System.currentTimeMillis()
            )
            firestoreManager.getCollection("reports").add(report).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
