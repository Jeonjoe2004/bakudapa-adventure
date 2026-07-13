package com.bakudapa.adventure.di

import com.bakudapa.adventure.core.dispatcher.DefaultDispatcherProvider
import com.bakudapa.adventure.core.dispatcher.DispatcherProvider
import com.bakudapa.adventure.core.network.ConnectivityManagerNetworkMonitor
import com.bakudapa.adventure.core.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Main Hilt Module for providing core application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    companion object {
        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    }
}
