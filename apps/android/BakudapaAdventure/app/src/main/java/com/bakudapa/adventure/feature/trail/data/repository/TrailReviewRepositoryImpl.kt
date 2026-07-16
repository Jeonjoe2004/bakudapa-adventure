package com.bakudapa.adventure.feature.trail.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.trail.domain.model.TrailReview
import com.bakudapa.adventure.feature.trail.domain.repository.TrailReviewRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrailReviewRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : TrailReviewRepository {

    override fun getReviews(trailId: String): Flow<DataResult<List<TrailReview>>> = callbackFlow {
        trySend(DataResult.Loading)
        val ref = firestoreManager.getCollection("trail_reviews")
            .whereEqualTo("trailId", trailId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResult.Error(error))
                return@addSnapshotListener
            }
            val reviews = snapshot?.documents?.mapNotNull {
                it.toObject(TrailReview::class.java)?.copy(id = it.id)
            } ?: emptyList()
            trySend(DataResult.Success(reviews))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addReview(trailId: String, rating: Float, comment: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            val review = TrailReview(
                trailId = trailId,
                authorId = user.uid,
                authorName = user.displayName ?: "Anonymous",
                authorPhotoUrl = user.photoUrl?.toString(),
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            firestoreManager.getCollection("trail_reviews").add(review).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): DataResult<Unit> {
        return try {
            firestoreManager.getCollection("trail_reviews").document(reviewId).delete().await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
