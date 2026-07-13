package com.bakudapa.adventure.feature.tracking.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HikingRoute(
    val id: String = "",
    val name: String = "",
    val points: List<TrackingPoint> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationMillis: Long = 0,
    val avgSpeed: Float = 0f,
    val maxElevation: Double = 0.0,
    val minElevation: Double = 0.0,
    val calories: Int = 0,
    val startTime: Long = 0,
    val endTime: Long? = null
)

enum class TrackingStatus {
    IDLE, START, PAUSE, STOP
}
