package com.bakudapa.adventure.feature.map.di

import com.bakudapa.adventure.feature.map.data.repository.MapRepositoryImpl
import com.bakudapa.adventure.feature.map.domain.repository.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Binds
    @Singleton
    abstract fun bindMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ): MapRepository
}
