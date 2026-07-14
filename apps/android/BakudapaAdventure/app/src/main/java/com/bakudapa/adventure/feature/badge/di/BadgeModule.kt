package com.bakudapa.adventure.feature.badge.di

import com.bakudapa.adventure.feature.badge.data.repository.BadgeRepositoryImpl
import com.bakudapa.adventure.feature.badge.domain.repository.BadgeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BadgeModule {

    @Binds
    @Singleton
    abstract fun bindBadgeRepository(
        badgeRepositoryImpl: BadgeRepositoryImpl
    ): BadgeRepository
}
