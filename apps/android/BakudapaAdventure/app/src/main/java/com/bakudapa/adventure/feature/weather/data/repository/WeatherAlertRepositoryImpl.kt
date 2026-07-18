package com.bakudapa.adventure.feature.weather.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.weather.data.remote.WeatherAlertApi
import com.bakudapa.adventure.feature.weather.domain.model.WeatherAlert
import com.bakudapa.adventure.feature.weather.domain.repository.WeatherAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherAlertRepositoryImpl @Inject constructor(
    private val api: WeatherAlertApi
) : WeatherAlertRepository {

    override fun getWeatherAlerts(lat: Double, lon: Double): Flow<DataResult<List<WeatherAlert>>> = callbackFlow {
        trySend(DataResult.Success(emptyList()))
    }
}