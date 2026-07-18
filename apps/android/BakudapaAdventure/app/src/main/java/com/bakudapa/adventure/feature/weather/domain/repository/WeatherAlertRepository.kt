package com.bakudapa.adventure.feature.weather.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.weather.domain.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface WeatherAlertRepository {
    fun getWeatherAlerts(lat: Double, lon: Double): Flow<DataResult<List<WeatherAlert>>>
}