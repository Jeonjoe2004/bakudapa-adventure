package com.bakudapa.adventure.feature.premium.di

import com.bakudapa.adventure.feature.premium.data.repository.PremiumRepositoryImpl
import com.bakudapa.adventure.feature.premium.domain.repository.PremiumRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PremiumModule {

    @Binds
    @Singleton
    abstract fun bindPremiumRepository(
        impl: PremiumRepositoryImpl
    ): PremiumRepository
}
