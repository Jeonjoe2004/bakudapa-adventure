package com.bakudapa.adventure.core.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring the device network connectivity status.
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
