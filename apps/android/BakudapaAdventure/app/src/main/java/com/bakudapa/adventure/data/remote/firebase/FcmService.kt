package com.bakudapa.adventure.data.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bakudapa.adventure.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Messaging Service for handling push notifications
 */
class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server if needed
        sendTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload
        remoteMessage.data?.let { data ->
            val type = data["type"] ?: "default"
            when (type) {
                "weather_alert" -> handleWeatherAlert(data)
                "summit_log" -> handleSummitLog(data)
                "trail_update" -> handleTrailUpdate(data)
                "sos_alert" -> handleSosAlert(data)
                else -> showDefaultNotification(data)
            }
        }

        // Also check notification payload
        remoteMessage.notification?.let { notification ->
            showNotification(
                notification.title ?: "Bakudapa Adventure",
                notification.body ?: "",
                "default"
            )
        }
    }

    private fun handleWeatherAlert(data: Map<String, String>) {
        val event = data["event"] ?: "Weather Alert"
        val description = data["description"] ?: "Weather conditions may affect your hike"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notification_type", "weather_alert")
            putExtra("notification_event", data["event"] ?: "")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        showNotificationWithIntent(
            title = "⚠️ $event",
            body = description,
            intent = intent,
            channelId = "weather_alerts",
            channelName = "Weather Alerts",
            importance = NotificationManager.IMPORTANCE_HIGH
        )
    }

    private fun handleSummitLog(data: Map<String, String>) {
        val mountainName = data["mountain"] ?: "Unknown Mountain"
        val userName = data["user"] ?: "Someone"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notification_type", "summit_log")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        showNotificationWithIntent(
            title = "🏔️ Summit Check-in",
            body = "$userName reached the summit of $mountainName!",
            intent = intent,
            channelId = "summit_logs",
            channelName = "Summit Logs",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    private fun handleTrailUpdate(data: Map<String, String>) {
        val trailName = data["trail"] ?: "Trail"
        val status = data["status"] ?: "updated"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notification_type", "trail_update")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        showNotificationWithIntent(
            title = "🥾 Trail Update: $trailName",
            body = "Trail status: $status",
            intent = intent,
            channelId = "trail_updates",
            channelName = "Trail Updates",
            importance = NotificationManager.IMPORTANCE_DEFAULT
        )
    }

    private fun handleSosAlert(data: Map<String, String>) {
        val senderName = data["sender"] ?: "Someone"
        val lat = data["lat"]?.toDoubleOrNull()
        val lon = data["lon"]?.toDoubleOrNull()

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("show_sos", true)
            lat?.let { putExtra("lat", it) }
            lon?.let { putExtra("lon", it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        showNotificationWithIntent(
            title = "🚨 EMERGENCY SOS",
            body = "$senderName needs help! ${lat?.let { "Lat: $it, Lon: $lon" } ?: ""}",
            intent = intent,
            channelId = "sos_alerts",
            channelName = "SOS Alerts",
            importance = NotificationManager.IMPORTANCE_MAX,
            notificationId = notificationId.hashCode(),
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
    }

    private fun showDefaultNotification(data: Map<String, String>) {
        showNotification(
            title = data["title"] ?: "Bakudapa Adventure",
            body = data["body"] ?: data["message"] ?: "New notification",
            channelId = "default"
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        channelId: String,
        channelName: String = "Notifications",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt(),
        soundUri: Uri? = null
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        showNotificationWithIntent(
            title = title,
            body = body,
            intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            channelId = channelId,
            channelName = channelId,
            importance = importance,
            soundUri = soundUri
        )
    }

    private fun showNotificationWithIntent(
        title: String,
        body: String,
        intent: Intent,
        channelId: String,
        channelName: String,
        importance: Int,
        notificationId: Int = (System.currentTimeMillis() % 100000).toInt(),
        soundUri: Uri? = null
    ) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val sound = soundUri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(importance)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(sound)
            .setVibrate(longArrayOf(0, 500, 200, 500))

        // Create notification channel if needed
        createNotificationChannel(channelId, channelName, importance)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notifications for $channelName"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        // TODO: Send token to backend for targeted notifications
    }

    companion object {
        fun subscribeToTopic(topic: String) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }

        fun unsubscribeFromTopic(topic: String) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
        }
    }
}