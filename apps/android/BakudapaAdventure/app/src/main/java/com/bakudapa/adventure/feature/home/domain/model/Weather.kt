package com.bakudapa.adventure.feature.home.domain.model

data class Weather(
    val temperature: Float,
    val condition: String,
    val iconUrl: String,
    val humidity: Int,
    val windSpeed: Float
)
