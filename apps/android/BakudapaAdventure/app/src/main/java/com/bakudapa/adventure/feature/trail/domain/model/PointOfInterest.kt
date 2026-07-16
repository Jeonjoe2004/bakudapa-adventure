package com.bakudapa.adventure.feature.trail.domain.model

data class PointOfInterest(
    val name: String,
    val type: PoiType,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Int = 0,
    val description: String = "",
)

enum class PoiType {
    CAMPING_GROUND,
    WATER_SOURCE,
    SHELTER,
    SUMMIT,
    DANGER_ZONE,
    TRAIL_HEAD,
}
