package com.bakudapa.adventure.feature.leaderboard.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardEntry
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardFilter

data class LeaderboardState(
    val isLoading: Boolean = false,
    val entries: List<LeaderboardEntry> = emptyList(),
    val selectedFilter: LeaderboardFilter = LeaderboardFilter.CLIMBS,
    val error: String? = null
) : UiState

sealed class LeaderboardEvent : UiEvent {
    object LoadLeaderboard : LeaderboardEvent()
    data class OnFilterSelected(val filter: LeaderboardFilter) : LeaderboardEvent()
    data class OnUserClicked(val userId: String) : LeaderboardEvent()
}

sealed class LeaderboardEffect : UiEffect {
    data class NavigateToUser(val userId: String) : LeaderboardEffect()
    data class ShowError(val message: String) : LeaderboardEffect()
}
