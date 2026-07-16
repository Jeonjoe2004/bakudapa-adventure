package com.bakudapa.adventure.feature.leaderboard.di

import com.bakudapa.adventure.feature.leaderboard.data.repository.LeaderboardRepositoryImpl
import com.bakudapa.adventure.feature.leaderboard.domain.repository.LeaderboardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LeaderboardModule {

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(
        impl: LeaderboardRepositoryImpl
    ): LeaderboardRepository
}
