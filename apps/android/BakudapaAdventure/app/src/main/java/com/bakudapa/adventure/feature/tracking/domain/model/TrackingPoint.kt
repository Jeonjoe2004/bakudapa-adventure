package com.bakudapa.adventure.feature.tracking.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackingPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val speed: Float = 0f
)
