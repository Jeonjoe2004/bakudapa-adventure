package com.bakudapa.adventure.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level interface for core Firebase operations.
 */
interface FirebaseRepository {
    fun isUserAuthenticated(): Boolean
    fun getUserId(): String?
    suspend fun getFCMToken(): String?
    fun signOut()
}
