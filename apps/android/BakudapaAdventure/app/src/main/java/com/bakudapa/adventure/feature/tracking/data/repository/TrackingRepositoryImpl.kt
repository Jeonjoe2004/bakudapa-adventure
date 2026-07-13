package com.bakudapa.adventure.feature.tracking.data.repository

import android.content.Context
import android.content.Intent
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteEntity
import com.bakudapa.adventure.feature.tracking.data.service.HikingService
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hikingRouteDao: HikingRouteDao
) : TrackingRepository {

    private val _trackingStatus = MutableStateFlow(TrackingStatus.IDLE)
    override val trackingStatus: Flow<TrackingStatus> = _trackingStatus.asStateFlow()

    private val _currentRoute = MutableStateFlow(HikingRoute())
    override val currentRoute: Flow<HikingRoute> = _currentRoute.asStateFlow()

    override suspend fun startTracking() {
        _trackingStatus.value = TrackingStatus.START
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_START
        }
        context.startForegroundService(intent)
    }

    override suspend fun pauseTracking() {
        _trackingStatus.value = TrackingStatus.PAUSE
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    override suspend fun resumeTracking() {
        _trackingStatus.value = TrackingStatus.START
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_START
        }
        context.startService(intent)
    }

    override suspend fun stopTracking() {
        _trackingStatus.value = TrackingStatus.STOP
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_STOP
        }
        context.startService(intent)
    }

    override suspend fun saveRoute(route: HikingRoute): DataResult<Unit> {
        return try {
            hikingRouteDao.insertRoute(route.toEntity())
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getSavedRoutes(): Flow<DataResult<List<HikingRoute>>> =
        hikingRouteDao.getAllRoutes().map { entities ->
            DataResult.Success(entities.map { it.toDomain() })
        }.catch { emit(DataResult.Error(it)) }

    override suspend fun exportToGpx(route: HikingRoute): DataResult<String> {
        // Simple GPX export logic would go here
        return DataResult.Success("path/to/exported.gpx")
    }

    private fun HikingRoute.toEntity() = HikingRouteEntity(
        id = if (id.isBlank()) java.util.UUID.randomUUID().toString() else id,
        name = name,
        points = points,
        distanceMeters = distanceMeters,
        durationMillis = durationMillis,
        avgSpeed = avgSpeed,
        maxElevation = maxElevation,
        minElevation = minElevation,
        calories = calories,
        startTime = startTime,
        endTime = endTime
    )

    private fun HikingRouteEntity.toDomain() = HikingRoute(
        id = id,
        name = name,
        points = points,
        distanceMeters = distanceMeters,
        durationMillis = durationMillis,
        avgSpeed = avgSpeed,
        maxElevation = maxElevation,
        minElevation = minElevation,
        calories = calories,
        startTime = startTime,
        endTime = endTime
    )
}
