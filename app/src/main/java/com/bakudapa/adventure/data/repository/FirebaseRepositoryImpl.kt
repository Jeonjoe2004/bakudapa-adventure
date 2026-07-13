package com.bakudapa.adventure.data.repository

import com.bakudapa.adventure.data.remote.firebase.FirebaseAuthManager
import com.bakudapa.adventure.data.remote.firebase.NotificationManager
import com.bakudapa.adventure.domain.repository.FirebaseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [FirebaseRepository] using injected managers.
 */
@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val notificationManager: NotificationManager
) : FirebaseRepository {

    override fun isUserAuthenticated(): Boolean = authManager.currentUser != null

    override fun getUserId(): String? = authManager.currentUser?.uid

    override suspend fun getFCMToken(): String? = notificationManager.getFcmToken()

    override fun signOut() = authManager.signOut()
}
