package com.bakudapa.adventure.feature.map.domain.model

data class MapMarker(
    val id: String,
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val type: MarkerType,
    val elevation: Int? = null
)

enum class MarkerType {
    MOUNTAIN,
    SUMMIT,
    CAMPING_GROUND,
    WATER_SOURCE,
    DANGER_ZONE,
    TRAIL_HEAD
}
