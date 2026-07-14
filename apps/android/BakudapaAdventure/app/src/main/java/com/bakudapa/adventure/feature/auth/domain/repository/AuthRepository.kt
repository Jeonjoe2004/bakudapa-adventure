package com.bakudapa.adventure.feature.auth.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level interface for authentication operations.
 */
interface AuthRepository {
    val authUser: Flow<AuthUser?>
    
    suspend fun signIn(email: String, password: String): DataResult<AuthUser>
    suspend fun signUp(email: String, password: String): DataResult<AuthUser>
    suspend fun sendPasswordResetEmail(email: String): DataResult<Unit>
    suspend fun sendEmailVerification(): DataResult<Unit>
    suspend fun reloadUser(): DataResult<AuthUser?>
    suspend fun signOut()
    suspend fun completeProfile(displayName: String, photoUrl: String?): DataResult<Unit>
}
