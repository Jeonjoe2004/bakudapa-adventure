package com.bakudapa.adventure.feature.auth.domain.model

/**
 * Domain model representing an authenticated user.
 */
data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)
