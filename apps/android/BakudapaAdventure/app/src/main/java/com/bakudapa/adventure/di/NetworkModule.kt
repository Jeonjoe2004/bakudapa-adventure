package com.bakudapa.adventure.di

import com.bakudapa.adventure.feature.home.data.remote.WeatherApi
import com.bakudapa.adventure.feature.map.data.remote.OSRMService
import com.bakudapa.adventure.feature.weather.data.remote.WeatherAlertApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideWeatherApi(okHttpClient: OkHttpClient, json: Json): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOSRMService(okHttpClient: OkHttpClient, json: Json): OSRMService {
        return Retrofit.Builder()
            .baseUrl("https://router.project-osrm.org/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OSRMService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherAlertApi(okHttpClient: OkHttpClient, json: Json): WeatherAlertApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(WeatherAlertApi::class.java)
    }
}
