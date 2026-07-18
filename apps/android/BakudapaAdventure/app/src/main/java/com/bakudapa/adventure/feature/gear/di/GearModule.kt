package com.bakudapa.adventure.feature.gear.di

import com.bakudapa.adventure.feature.gear.data.repository.GearRepositoryImpl
import com.bakudapa.adventure.feature.gear.domain.repository.GearRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GearModule {
    @Binds
    @Singleton
    abstract fun bindGearRepository(impl: GearRepositoryImpl): GearRepository
}
