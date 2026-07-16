package com.bakudapa.adventure.feature.ai.di

import com.bakudapa.adventure.feature.ai.data.repository.AiRepositoryImpl
import com.bakudapa.adventure.feature.ai.domain.repository.AiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        impl: AiRepositoryImpl
    ): AiRepository
}
