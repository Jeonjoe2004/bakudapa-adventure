package com.bakudapa.adventure.feature.trail.di

import com.bakudapa.adventure.feature.trail.data.repository.TrailRepositoryImpl
import com.bakudapa.adventure.feature.trail.domain.repository.TrailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrailModule {

    @Binds
    @Singleton
    abstract fun bindTrailRepository(
        impl: TrailRepositoryImpl
    ): TrailRepository
}
