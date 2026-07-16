package com.bakudapa.adventure.feature.leaderboard.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardEntry
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getLeaderboard(): Flow<DataResult<List<LeaderboardEntry>>>
}
