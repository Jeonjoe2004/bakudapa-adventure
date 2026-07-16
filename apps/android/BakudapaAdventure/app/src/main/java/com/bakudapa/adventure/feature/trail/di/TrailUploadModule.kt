package com.bakudapa.adventure.feature.trail.di

import com.bakudapa.adventure.feature.trail.data.repository.TrailUploadRepositoryImpl
import com.bakudapa.adventure.feature.trail.domain.repository.TrailUploadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TrailUploadModule {

    @Binds
    @Singleton
    abstract fun bindTrailUploadRepository(
        impl: TrailUploadRepositoryImpl
    ): TrailUploadRepository
}
