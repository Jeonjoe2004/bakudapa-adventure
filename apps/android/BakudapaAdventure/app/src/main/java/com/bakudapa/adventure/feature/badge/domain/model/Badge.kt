package com.bakudapa.adventure.feature.badge.domain.model

data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val criteria: String = ""
)
