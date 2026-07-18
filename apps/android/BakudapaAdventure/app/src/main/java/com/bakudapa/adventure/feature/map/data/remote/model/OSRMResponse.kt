package com.bakudapa.adventure.feature.map.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OSRMResponse(
    val code: String,
    val routes: List<OSRMRoute>,
    val waypoints: List<OSRMWaypoint>
)

@Serializable
data class OSRMRoute(
    val distance: Double,
    val duration: Double,
    val geometry: String,
    val legs: List<OSRMLeg>,
    val weight: Double,
    val weight_name: String
)

@Serializable
data class OSRMLeg(
    val distance: Double,
    val duration: Double,
    val steps: List<OSRMStep>,
    val summary: String,
    val weight: Double,
    val duration_typical: Double
)

@Serializable
data class OSRMStep(
    val distance: Double,
    val duration: Double,
    val geometry: String,
    val maneuver: OSRMManeuver,
    val name: String,
    val mode: String,
    val weight: Double,
    val driving_side: String,
    val intersections: List<OSRMIntersection>
)

@Serializable
data class OSRMManeuver(
    val bearing_after: Double,
    val bearing_before: Double,
    val location: List<Double>,
    val type: String,
    val modifier: String?,
    val instruction: String?
)

@Serializable
data class OSRMIntersection(
    val location: List<Double>,
    val bearings: List<Double>,
    val entry: List<Boolean>,
    val `in`: Double,
    val out: Double,
    val is_urban: Boolean,
    val admin_index: Int,
    val outlet: Int
)

@Serializable
data class OSRMWaypoint(
    val name: String,
    val location: List<Double>,
    val distance: Double
)