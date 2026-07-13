package com.bakudapa.adventure.feature.tracking.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint

@Entity(tableName = "hiking_routes")
data class HikingRouteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val points: List<TrackingPoint>,
    val distanceMeters: Double,
    val durationMillis: Long,
    val avgSpeed: Float,
    val maxElevation: Double,
    val minElevation: Double,
    val calories: Int,
    val startTime: Long,
    val endTime: Long?
)
