package com.bakudapa.adventure.data.remote.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Firebase Cloud Messaging operations.
 */
@Singleton
class NotificationManager @Inject constructor(
    private val messaging: FirebaseMessaging
) {
    suspend fun getFcmToken(): String? {
        return try {
            messaging.token.await()
        } catch (e: Exception) {
            null
        }
    }

    fun subscribeToTopic(topic: String) = messaging.subscribeToTopic(topic)

    fun unsubscribeFromTopic(topic: String) = messaging.unsubscribeFromTopic(topic)
}
