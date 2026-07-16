package com.bakudapa.adventure.feature.leaderboard.domain.model

data class LeaderboardEntry(
    val userId: String = "",
    val displayName: String = "Adventurer",
    val photoUrl: String? = null,
    val totalDistanceKm: Double = 0.0,
    val totalElevationM: Int = 0,
    val mountainsClimbed: Int = 0,
    val badgesEarned: Int = 0,
    val rank: Int = 0
)

enum class LeaderboardFilter {
    DISTANCE, ELEVATION, CLIMBS, BADGES
}
