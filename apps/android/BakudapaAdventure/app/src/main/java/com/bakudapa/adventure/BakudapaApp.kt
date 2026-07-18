package com.bakudapa.adventure

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class BakudapaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel("weather_alerts", "Weather Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Severe weather warnings for your area"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                },
                NotificationChannel("summit_logs", "Summit Logs", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Community summit check-ins"
                },
                NotificationChannel("trail_updates", "Trail Updates", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Trail condition updates"
                },
                NotificationChannel("sos_alerts", "SOS Alerts", NotificationManager.IMPORTANCE_MAX).apply {
                    description = "Emergency SOS alerts"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                },
                NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
}
