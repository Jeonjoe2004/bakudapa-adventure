package com.bakudapa.adventure.feature.summit.di

import com.bakudapa.adventure.feature.summit.data.repository.SummitRepositoryImpl
import com.bakudapa.adventure.feature.summit.domain.repository.SummitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SummitModule {
    @Binds
    @Singleton
    abstract fun bindSummitRepository(impl: SummitRepositoryImpl): SummitRepository
}
