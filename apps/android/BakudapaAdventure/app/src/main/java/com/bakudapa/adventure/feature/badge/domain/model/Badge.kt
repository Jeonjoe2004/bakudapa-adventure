package com.bakudapa.adventure.feature.badge.domain.model

data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val criteria: String = "",
    val progress: Int = 0,
    val target: Int = 1
) {
    val progressPercent: Float
        get() = if (target > 0) (progress.toFloat() / target).coerceIn(0f, 1f) else 0f
}
