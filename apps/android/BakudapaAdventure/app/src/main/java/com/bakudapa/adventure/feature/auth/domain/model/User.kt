package com.bakudapa.adventure.feature.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?
)
