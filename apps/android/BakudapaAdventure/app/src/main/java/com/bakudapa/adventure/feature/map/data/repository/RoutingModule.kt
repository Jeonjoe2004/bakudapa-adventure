package com.bakudapa.adventure.feature.map.data.repository

import com.bakudapa.adventure.feature.map.domain.repository.RoutingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoutingModule {
    @Binds
    @Singleton
    abstract fun bindRoutingRepository(impl: RoutingRepositoryImpl): RoutingRepository
}