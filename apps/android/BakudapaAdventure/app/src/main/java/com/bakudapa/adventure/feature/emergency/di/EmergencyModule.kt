package com.bakudapa.adventure.feature.emergency.di

import com.bakudapa.adventure.feature.emergency.data.repository.EmergencyRepositoryImpl
import com.bakudapa.adventure.feature.emergency.domain.repository.EmergencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EmergencyModule {

    @Binds
    @Singleton
    abstract fun bindEmergencyRepository(
        emergencyRepositoryImpl: EmergencyRepositoryImpl
    ): EmergencyRepository
}
