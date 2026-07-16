package com.bakudapa.adventure.feature.mountain.domain.model

data class Mountain(
    val id: String,
    val name: String,
    val location: String,
    val elevation: Int,
    val imageUrl: String,
    val rating: Float,
    val difficulty: MountainDifficulty = MountainDifficulty.MODERATE,
    val distance: Double? = null,
)