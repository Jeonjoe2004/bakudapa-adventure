package com.bakudapa.adventure.feature.emergency.domain.model

data class SOSAlert(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = "SOS! I need help!",
    val isActive: Boolean = true
)
