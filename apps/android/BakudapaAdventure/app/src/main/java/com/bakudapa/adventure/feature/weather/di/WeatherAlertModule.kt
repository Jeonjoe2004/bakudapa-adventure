package com.bakudapa.adventure.feature.weather.di

import com.bakudapa.adventure.feature.weather.data.repository.WeatherAlertRepositoryImpl
import com.bakudapa.adventure.feature.weather.domain.repository.WeatherAlertRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherAlertModule {
    @Binds
    @Singleton
    abstract fun bindWeatherAlertRepository(
        impl: WeatherAlertRepositoryImpl
    ): WeatherAlertRepository
}