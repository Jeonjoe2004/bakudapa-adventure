package com.bakudapa.adventure.feature.home.domain.model

data class Trail(
    val id: String,
    val name: String,
    val mountainName: String,
    val difficulty: TrailDifficulty,
    val durationMinutes: Int,
    val distanceKm: Double,
    val imageUrl: String
)

enum class TrailDifficulty {
    EASY, MODERATE, HARD, EXPERT
}
