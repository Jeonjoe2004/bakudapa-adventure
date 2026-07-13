package com.bakudapa.adventure.feature.tracking.di

import com.bakudapa.adventure.feature.tracking.data.repository.TrackingRepositoryImpl
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrackingModule {

    @Binds
    @Singleton
    abstract fun bindTrackingRepository(
        trackingRepositoryImpl: TrackingRepositoryImpl
    ): TrackingRepository
}
