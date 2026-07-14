package com.bakudapa.adventure.feature.mountain.di

import com.bakudapa.adventure.feature.mountain.data.repository.MountainRepositoryImpl
import com.bakudapa.adventure.feature.mountain.domain.repository.MountainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MountainModule {

    @Binds
    @Singleton
    abstract fun bindMountainRepository(
        impl: MountainRepositoryImpl
    ): MountainRepository
}
