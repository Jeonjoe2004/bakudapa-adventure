package com.bakudapa.adventure.feature.ai.domain.model

data class RouteRecommendation(
    val trailId: String,
    val trailName: String,
    val mountainName: String,
    val score: Float,
    val reasons: List<String>,
    val estimatedDurationMinutes: Int,
    val difficultyMatch: Float,
    val seasonMatch: Boolean,
)

data class SafetyAlert(
    val type: SafetyAlertType,
    val severity: SafetySeverity,
    val message: String,
    val recommendation: String,
    val trailId: String? = null,
    val trailName: String? = null,
)

enum class SafetyAlertType {
    WEATHER_WARNING,
    LATENESS_WARNING,
    ELEVATION_GAIN_ALERT,
    DISTANCE_ALERT,
    OFF_TRAIL,
    LOW_BATTERY,
    DARKNESS_UPCOMING,
    HIGH_CROWD_DENSITY,
}

enum class SafetySeverity {
    INFO, WARNING, CRITICAL
}

data class TrailAnalysis(
    val trailId: String,
    val avgDurationMinutes: Double,
    val successRate: Float,
    val avgRating: Float,
    val bestSeason: String?,
    val crowdLevel: CrowdLevel,
    val difficultyDistribution: Map<String, Int> = emptyMap(),
)

enum class CrowdLevel {
    EMPTY, LIGHT, MODERATE, BUSY, PEAK
}
