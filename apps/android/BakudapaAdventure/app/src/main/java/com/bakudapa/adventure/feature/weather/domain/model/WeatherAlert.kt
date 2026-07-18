package com.bakudapa.adventure.feature.weather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherAlert(
    val senderName: String = "",
    val event: String = "",
    val start: Long = 0L,
    val end: Long = 0L,
    val description: String = "",
    val tags: List<String> = emptyList()
)