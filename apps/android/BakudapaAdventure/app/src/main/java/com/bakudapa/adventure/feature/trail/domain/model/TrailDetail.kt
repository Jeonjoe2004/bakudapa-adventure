package com.bakudapa.adventure.feature.trail.domain.model

import com.bakudapa.adventure.feature.mountain.domain.model.MountainDifficulty

data class Checkpoint(
    val name: String,
    val elevation: Int,
    val eta: String,
)

data class TrailDetail(
    val id: String,
    val name: String,
    val mountainId: String,
    val mountainName: String,
    val difficulty: MountainDifficulty = MountainDifficulty.MODERATE,
    val durationMinutes: Int,
    val distanceKm: Double,
    val imageUrl: String,
    val description: String = "",
    val popularity: Int = 0,
    val elevationGain: Int = 0,
    val maxElevation: Int = 0,
    val recommendedGear: List<String> = emptyList(),
    val pointsOfInterest: List<PointOfInterest> = emptyList(),
    val checkpoints: List<Checkpoint> = emptyList(),
)
