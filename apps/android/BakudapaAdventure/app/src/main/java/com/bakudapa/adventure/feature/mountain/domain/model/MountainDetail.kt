package com.bakudapa.adventure.feature.mountain.domain.model

import com.bakudapa.adventure.feature.home.domain.model.Mountain

data class MountainDetail(
    val id: String,
    val name: String,
    val location: String,
    val elevation: Int,
    val imageUrl: String,
    val rating: Float,
    val description: String = "",
    val difficulty: String = "",
    val bestSeason: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: Double? = null,
    val weatherTip: String = "",
) {
    fun toBasicMountain() = Mountain(id, name, location, elevation, imageUrl, rating, distance)
}

data class TrailInfo(
    val id: String,
    val name: String,
    val mountainName: String,
    val difficulty: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val imageUrl: String,
    val description: String = "",
)

data class MountainSection(
    val id: String,
    val name: String,
    val elevation: Int,
    val type: SectionType,
)

enum class SectionType { POS, CAMPGROUND, WATER_SOURCE, SUMMIT, SHELTER }
