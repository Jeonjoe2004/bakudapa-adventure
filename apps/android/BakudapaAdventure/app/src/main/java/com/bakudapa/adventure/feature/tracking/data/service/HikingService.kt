package com.bakudapa.adventure.feature.tracking.data.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.bakudapa.adventure.R
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingPoint
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class HikingService : Service() {

    @Inject
    lateinit var trackingRepository: TrackingRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isTracking = false

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START -> startTracking()
                ACTION_PAUSE -> pauseTracking()
                ACTION_STOP -> stopTracking()
            }
        }
        return START_STICKY
    }

    private fun startTracking() {
        isTracking = true
        startForeground(NOTIFICATION_ID, createNotification())
        requestLocationUpdates()
    }

    private fun pauseTracking() {
        isTracking = false
        // Keep foreground service but stop updating location list
    }

    private fun stopTracking() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                null
            )
        } catch (e: SecurityException) {
            // Log error
        }
    }

    private fun updateLocation(location: Location) {
        serviceScope.launch {
            val point = TrackingPoint(
                latitude = location.latitude,
                longitude = location.longitude,
                elevation = location.altitude,
                speed = location.speed,
                timestamp = location.time
            )
            // This would ideally go through a private method in the repository impl
            // but since I'm defining the interface, I'll need a way to feed data to it.
        }
    }

    private fun createNotification(): Notification {
        val channelId = "hiking_tracking"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hiking Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking your Hike")
            .setContentText("Distance: 0.0 km")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a proper icon
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
