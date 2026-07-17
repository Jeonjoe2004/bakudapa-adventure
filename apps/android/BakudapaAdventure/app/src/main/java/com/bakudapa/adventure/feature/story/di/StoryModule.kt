package com.bakudapa.adventure.feature.story.di

import com.bakudapa.adventure.feature.story.data.repository.StoryRepositoryImpl
import com.bakudapa.adventure.feature.story.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryModule {
    @Binds
    @Singleton
    abstract fun bindStoryRepository(impl: StoryRepositoryImpl): StoryRepository
}
