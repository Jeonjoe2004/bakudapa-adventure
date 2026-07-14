package com.bakudapa.adventure.feature.tracking.data.repository

import android.content.Context
import android.content.Intent
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteDao
import com.bakudapa.adventure.feature.tracking.data.local.HikingRouteEntity
import com.bakudapa.adventure.feature.tracking.data.service.HikingService
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

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
        // Reset route untuk sesi baru
        _currentRoute.value = HikingRoute(
            id = UUID.randomUUID().toString(),
            startTime = System.currentTimeMillis()
        )
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
        // Set endTime sebelum stop
        _currentRoute.value = _currentRoute.value.copy(
            endTime = System.currentTimeMillis(),
            durationMillis = System.currentTimeMillis() - _currentRoute.value.startTime
        )
        val intent = Intent(context, HikingService::class.java).apply {
            action = HikingService.ACTION_STOP
        }
        context.startService(intent)
    }

    override suspend fun addTrackingPoint(point: TrackingPoint) {
        val current = _currentRoute.value
        val updatedPoints = current.points + point

        val distanceMeters = calculateTotalDistance(updatedPoints)
        val durationMillis = System.currentTimeMillis() - current.startTime
        val maxElevation = updatedPoints.maxOfOrNull { it.elevation } ?: 0.0
        val minElevation = updatedPoints.minOfOrNull { it.elevation } ?: 0.0
        val avgSpeed = calculateAverageSpeed(updatedPoints)
        val calories = calculateCalories(distanceMeters)

        _currentRoute.value = current.copy(
            points = updatedPoints,
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            maxElevation = maxElevation,
            minElevation = minElevation,
            avgSpeed = avgSpeed,
            calories = calories
        )
    }

    override suspend fun saveRoute(route: HikingRoute): DataResult<Unit> {
        return try {
            hikingRouteDao.insertRoute(route.toEntity())
            // Reset current route setelah disimpan
            _currentRoute.value = HikingRoute()
            _trackingStatus.value = TrackingStatus.IDLE
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
        return try {
            val gpx = buildGpxString(route)
            val fileName = "route_${route.id}.gpx"
            val file = java.io.File(context.getExternalFilesDir(null), fileName)
            file.writeText(gpx)
            DataResult.Success(file.absolutePath)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    // ---- Calculation Helpers ----

    private fun calculateTotalDistance(points: List<TrackingPoint>): Double {
        if (points.size < 2) return 0.0
        var total = 0.0
        for (i in 0 until points.size - 1) {
            total += haversineDistance(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude
            )
        }
        return total
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // meter
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun calculateAverageSpeed(points: List<TrackingPoint>): Float {
        if (points.isEmpty()) return 0f
        return points.map { it.speed }.average().toFloat()
    }

    private fun calculateCalories(distanceMeters: Double): Int {
        // Estimasi: ~60 kalori per km berjalan kaki
        return ((distanceMeters / 1000.0) * 60).toInt()
    }

    private fun buildGpxString(route: HikingRoute): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gpx version="1.1" creator="BakudapaAdventure">""")
        sb.appendLine("""  <trk><name>${route.name}</name><trkseg>""")
        route.points.forEach { pt ->
            sb.appendLine("""    <trkpt lat="${pt.latitude}" lon="${pt.longitude}">""")
            sb.appendLine("""      <ele>${pt.elevation}</ele>""")
            sb.appendLine("""    </trkpt>""")
        }
        sb.appendLine("""  </trkseg></trk>""")
        sb.appendLine("""</gpx>""")
        return sb.toString()
    }

    // ---- Mappers ----

    private fun HikingRoute.toEntity() = HikingRouteEntity(
        id = if (id.isBlank()) UUID.randomUUID().toString() else id,
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
