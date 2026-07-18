package com.bakudapa.adventure.feature.weather.data.remote

import com.bakudapa.adventure.feature.weather.data.remote.model.WeatherAlertResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAlertApi {
    @GET("onecall")
    fun getWeatherAlerts(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,minutely,hourly,daily",
        @Query("appid") apiKey: String
    ): Call<WeatherAlertResponse>
}