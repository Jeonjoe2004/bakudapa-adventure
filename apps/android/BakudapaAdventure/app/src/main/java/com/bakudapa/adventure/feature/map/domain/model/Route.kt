package com.bakudapa.adventure.feature.map.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val distance: Double, // meters
    val duration: Long, // seconds
    val geometry: String, // encoded polyline
    val legs: List<RouteLeg>
)

@Serializable
data class RouteLeg(
    val distance: Double,
    val duration: Long,
    val steps: List<RouteStep>,
    val summary: String
)

@Serializable
data class RouteStep(
    val distance: Double,
    val duration: Long,
    val instruction: String,
    val name: String,
    val geometry: String // encoded polyline for this step
)