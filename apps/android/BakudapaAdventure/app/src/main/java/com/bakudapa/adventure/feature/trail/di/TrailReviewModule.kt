package com.bakudapa.adventure.feature.trail.di

import com.bakudapa.adventure.feature.trail.data.repository.TrailReviewRepositoryImpl
import com.bakudapa.adventure.feature.trail.domain.repository.TrailReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrailReviewModule {

    @Binds
    @Singleton
    abstract fun bindTrailReviewRepository(
        impl: TrailReviewRepositoryImpl
    ): TrailReviewRepository
}
