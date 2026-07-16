package com.bakudapa.adventure.feature.profile.domain.model

data class FollowRelation(
    val followerId: String = "",
    val followingId: String = "",
    val followedAt: Long = System.currentTimeMillis()
)

data class FollowUser(
    val id: String = "",
    val name: String = "",
    val photoUrl: String? = null
)
