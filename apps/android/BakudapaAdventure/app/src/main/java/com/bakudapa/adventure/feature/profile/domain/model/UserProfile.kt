package com.bakudapa.adventure.feature.profile.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val level: Int = 1,
    val xp: Int = 0,
    val stats: UserStats = UserStats()
)

data class UserStats(
    val totalDistanceKm: Double = 0.0,
    val totalElevationM: Int = 0,
    val mountainsClimbed: Int = 0,
    val totalHikingHours: Double = 0.0
)
