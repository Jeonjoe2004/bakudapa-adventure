package com.bakudapa.adventure.feature.trail.domain.model

data class TrailUpload(
    val name: String = "",
    val mountainId: String = "",
    val mountainName: String = "",
    val difficulty: String = "MODERATE",
    val durationMinutes: Int = 0,
    val distanceKm: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val elevationGain: Int = 0,
    val maxElevation: Int = 0,
    val recommendedGear: List<String> = emptyList(),
    val pointsOfInterest: List<PointOfInterest> = emptyList(),
    val status: String = "pending",
    val authorId: String = "",
    val authorName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
