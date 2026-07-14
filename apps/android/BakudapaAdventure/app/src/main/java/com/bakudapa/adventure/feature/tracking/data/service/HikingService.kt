package com.bakudapa.adventure.feature.tracking.data.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bakudapa.adventure.R
import com.bakudapa.adventure.feature.tracking.data.sensor.ElevationManager
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import javax.inject.Inject

@AndroidEntryPoint
class HikingService : Service() {

    @Inject
    lateinit var trackingRepository: TrackingRepository
    
    @Inject
    lateinit var elevationManager: ElevationManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private var isTracking = false
    private var currentBarometerAltitude: Double = 0.0

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking) {
                result.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        observeBarometer()
    }

    private fun observeBarometer() {
        serviceScope.launch {
            elevationManager.altitudeFlow.collectLatest { altitude ->
                currentBarometerAltitude = altitude
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> startTracking()
                ACTION_PAUSE -> pauseTracking()
                ACTION_STOP  -> stopTracking()
            }
        }
        return START_STICKY
    }

    private fun startTracking() {
        isTracking = true
        startForeground(NOTIFICATION_ID, buildNotification("0.00 km", "00:00:00"))
        requestLocationUpdates()
    }

    private fun pauseTracking() {
        isTracking = false
        updateNotification("Tracking dijeda", "--", "--")
    }

    private fun stopTracking() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, mainLooper)
        } catch (e: SecurityException) { }
    }

    private fun updateLocation(location: Location) {
        serviceScope.launch {
            // Use barometer altitude if available, fallback to GPS altitude
            val finalAltitude = if (elevationManager.hasBarometer()) currentBarometerAltitude else location.altitude
            
            val point = TrackingPoint(
                latitude = location.latitude,
                longitude = location.longitude,
                elevation = finalAltitude,
                speed = location.speed,
                timestamp = location.time
            )

            trackingRepository.addTrackingPoint(point)

            trackingRepository.currentRoute.take(1).collect { route ->
                val distanceKm = "%.2f km".format(route.distanceMeters / 1000.0)
                val duration = formatDuration(route.durationMillis)
                updateNotification("Sedang tracking...", distanceKm, duration)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hiking Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifikasi untuk tracking pendakian aktif"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(distance: String, duration: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking Pendakian Aktif")
            .setContentText("Jarak: $distance  •  Waktu: $duration")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(title: String, distance: String, duration: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("Jarak: $distance  •  Waktu: $duration")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours   = (millis / (1000 * 60 * 60))
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID     = "hiking_tracking"
        const val ACTION_START   = "ACTION_START"
        const val ACTION_PAUSE   = "ACTION_PAUSE"
        const val ACTION_STOP    = "ACTION_STOP"
    }
}
