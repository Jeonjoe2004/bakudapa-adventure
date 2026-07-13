package com.bakudapa.adventure.feature.profile.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String): Flow<DataResult<UserProfile>>
    fun getMyPosts(userId: String): Flow<DataResult<List<Post>>>
    fun getMyRoutes(userId: String): Flow<DataResult<List<HikingRoute>>>
    suspend fun updateProfile(name: String, photoUrl: String?): DataResult<Unit>
}
