package com.bakudapa.adventure.feature.profile.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.FollowUser
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getUserProfile(userId: String): Flow<DataResult<UserProfile>>
    fun getMyPosts(userId: String): Flow<DataResult<List<Post>>>
    fun getMyRoutes(userId: String): Flow<DataResult<List<HikingRoute>>>
    suspend fun updateProfile(name: String, username: String, bio: String, website: String, photoUrl: String?): DataResult<Unit>

    // Search users
    suspend fun searchUsers(query: String): DataResult<List<FollowUser>>

    // Follow system
    suspend fun followUser(targetUserId: String): DataResult<Unit>
    suspend fun unfollowUser(targetUserId: String): DataResult<Unit>
    fun isFollowing(targetUserId: String): Flow<DataResult<Boolean>>
    fun getFollowers(userId: String): Flow<DataResult<List<FollowUser>>>
    fun getFollowing(userId: String): Flow<DataResult<List<FollowUser>>>
    fun getFollowersCount(userId: String): Flow<DataResult<Int>>
    fun getFollowingCount(userId: String): Flow<DataResult<Int>>
}
