package com.bakudapa.adventure.feature.trail.domain.model

data class TrailDetail(
    val id: String,
    val name: String,
    val mountainId: String,
    val mountainName: String,
    val difficulty: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val imageUrl: String,
    val description: String = "",
    val popularity: Int = 0,
    val elevationGain: Int = 0,
    val maxElevation: Int = 0,
    val recommendedGear: List<String> = emptyList(),
    val waterSources: List<String> = emptyList(),
    val campingSpots: List<String> = emptyList(),
)
