package com.bakudapa.adventure.feature.trail.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.trail.domain.model.TrailReview
import kotlinx.coroutines.flow.Flow

interface TrailReviewRepository {
    fun getReviews(trailId: String): Flow<DataResult<List<TrailReview>>>
    suspend fun addReview(trailId: String, rating: Float, comment: String): DataResult<Unit>
    suspend fun deleteReview(reviewId: String): DataResult<Unit>
}
