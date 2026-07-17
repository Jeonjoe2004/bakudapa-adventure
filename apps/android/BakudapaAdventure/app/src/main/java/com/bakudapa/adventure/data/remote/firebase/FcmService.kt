package com.bakudapa.adventure.data.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bakudapa.adventure.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FcmService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Simpan token ke Firestore
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid ?: return@launch
                val fs = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                fs.collection("users").document(uid)
                    .collection("fcmTokens")
                    .document(token.hashCode().toString())
                    .set(mapOf("token" to token, "createdAt" to System.currentTimeMillis()))
                    .await()
            } catch (_: Exception) {}
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "Bakudapa Adventure"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "general"
        val targetId = message.data["targetId"] ?: ""

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notificationType", type)
            putExtra("targetId", targetId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when (type) {
            "like" -> "social_channel"
            "comment" -> "social_channel"
            "follow" -> "social_channel"
            "sos" -> "emergency_channel"
            else -> "general_channel"
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channels = listOf(
            NotificationChannel("general_channel", "Notifikasi", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notifikasi umum"
            },
            NotificationChannel("social_channel", "Aktivitas Sosial", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Like, komentar, dan follow"
            },
            NotificationChannel("emergency_channel", "Darurat", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Peringatan darurat dan SOS"
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channels.forEach { manager.createNotificationChannel(it) }
        }
    }
}
