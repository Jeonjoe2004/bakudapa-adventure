package com.bakudapa.adventure.feature.leaderboard.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardFilter
import com.bakudapa.adventure.feature.leaderboard.domain.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : BaseViewModel<LeaderboardState, LeaderboardEvent, LeaderboardEffect>(LeaderboardState()) {

    init {
        loadLeaderboard()
    }

    override fun onEvent(event: LeaderboardEvent) {
        when (event) {
            LeaderboardEvent.LoadLeaderboard -> loadLeaderboard()
            is LeaderboardEvent.OnFilterSelected -> setState { it.copy(selectedFilter = event.filter) }
            is LeaderboardEvent.OnUserClicked -> sendEffect(LeaderboardEffect.NavigateToUser(event.userId))
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            repository.getLeaderboard().collectLatest { result ->
                when (result) {
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                    is DataResult.Success -> setState {
                        it.copy(isLoading = false, entries = result.data, error = null)
                    }
                    is DataResult.Error -> setState {
                        it.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
        }
    }

    fun getSortedEntries(): List<com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardEntry> {
        val state = uiState.value
        return when (state.selectedFilter) {
            LeaderboardFilter.CLIMBS -> state.entries.sortedByDescending { it.mountainsClimbed }
            LeaderboardFilter.DISTANCE -> state.entries.sortedByDescending { it.totalDistanceKm }
            LeaderboardFilter.ELEVATION -> state.entries.sortedByDescending { it.totalElevationM }
            LeaderboardFilter.BADGES -> state.entries.sortedByDescending { it.badgesEarned }
        }.mapIndexed { i, e -> e.copy(rank = i + 1) }
    }
}
