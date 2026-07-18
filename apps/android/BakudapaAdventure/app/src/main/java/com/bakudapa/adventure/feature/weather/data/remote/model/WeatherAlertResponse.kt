package com.bakudapa.adventure.feature.weather.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherAlertResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val alerts: List<WeatherAlertItem> = emptyList()
)

@Serializable
data class WeatherAlertItem(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String> = emptyList()
)