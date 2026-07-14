package com.bakudapa.adventure.feature.badge.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.badge.domain.model.Badge
import kotlinx.coroutines.flow.Flow

interface BadgeRepository {
    fun getMyBadges(): Flow<DataResult<List<Badge>>>
    fun getAllBadges(): Flow<DataResult<List<Badge>>>
    suspend fun unlockBadge(badgeId: String): DataResult<Unit>
}
