package com.bakudapa.adventure.feature.premium.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.premium.domain.model.PremiumPlan
import com.bakudapa.adventure.feature.premium.domain.model.PremiumSubscription
import kotlinx.coroutines.flow.Flow

interface PremiumRepository {
    fun getSubscription(): Flow<DataResult<PremiumSubscription?>>
    suspend fun subscribe(planId: String): DataResult<Unit>
    suspend fun cancelSubscription(): DataResult<Unit>
    suspend fun backupToCloud(): DataResult<Unit>
    suspend fun restoreFromCloud(): DataResult<Unit>
    fun getSyncStatus(): Flow<DataResult<SyncStatus>>
    suspend fun getDetailedStats(userId: String): DataResult<DetailedStats>
}

data class SyncStatus(
    val lastSyncTime: Long? = null,
    val isSyncing: Boolean = false,
    val pendingItems: Int = 0,
    val totalBackedUp: Int = 0,
)

data class DetailedStats(
    val totalRoutes: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val totalElevationGainM: Int = 0,
    val totalDurationHours: Double = 0.0,
    val totalCaloriesBurned: Int = 0,
    val mountainsConquered: Int = 0,
    val averagePaceMinPerKm: Double = 0.0,
    val longestRouteKm: Double = 0.0,
    val highestElevationM: Int = 0,
    val recentActivity: List<ActivitySummary> = emptyList(),
    val monthlyStats: List<MonthlyStats> = emptyList(),
)

data class ActivitySummary(
    val date: Long,
    val routeName: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val mountainName: String,
)

data class MonthlyStats(
    val month: String,
    val year: Int,
    val totalDistanceKm: Double,
    val totalDurationMinutes: Int,
    val routeCount: Int,
)
