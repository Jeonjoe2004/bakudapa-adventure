package com.bakudapa.adventure.feature.auth.domain.repository

import com.bakudapa.adventure.core.data.Resource
import com.bakudapa.adventure.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): User?
    suspend fun signIn(email: String, pass: String): Resource<User>
    suspend fun signUp(email: String, pass: String, name: String): Resource<User>
    suspend fun signOut()
}
