package com.bakudapa.adventure.feature.event.di

import com.bakudapa.adventure.feature.event.data.repository.EventRepositoryImpl
import com.bakudapa.adventure.feature.event.domain.repository.BookingRepository
import com.bakudapa.adventure.feature.event.domain.repository.EventRepository
import com.bakudapa.adventure.feature.event.domain.repository.MarketplaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindMarketplaceRepository(impl: EventRepositoryImpl): MarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(impl: EventRepositoryImpl): BookingRepository
}
