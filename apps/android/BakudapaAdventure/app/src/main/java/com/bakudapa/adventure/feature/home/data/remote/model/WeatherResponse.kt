package com.bakudapa.adventure.feature.home.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("main") val main: Main,
    @SerialName("weather") val weather: List<WeatherInfo>,
    @SerialName("wind") val wind: Wind,
    @SerialName("name") val cityName: String
)

@Serializable
data class Main(
    @SerialName("temp") val temp: Float,
    @SerialName("humidity") val humidity: Int
)

@Serializable
data class WeatherInfo(
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String
)

@Serializable
data class Wind(
    @SerialName("speed") val speed: Float
)
