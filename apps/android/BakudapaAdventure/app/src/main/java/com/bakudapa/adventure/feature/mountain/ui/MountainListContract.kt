package com.bakudapa.adventure.feature.mountain.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.mountain.domain.model.Mountain
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDifficulty

data class MountainListState(
    val isLoading: Boolean = false,
    val mountains: List<Mountain> = emptyList(),
    val filteredMountains: List<Mountain> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedDifficulty: MountainDifficulty? = null,
    val minElevation: Int = 0,
    val maxElevation: Int = 0,
    val error: String? = null
) : UiState

sealed class MountainListEvent : UiEvent {
    object LoadMountains : MountainListEvent()
    data class OnSearchQueryChanged(val query: String) : MountainListEvent()
    object OnSearchClicked : MountainListEvent()
    data class OnSearchActiveChanged(val active: Boolean) : MountainListEvent()
    data class OnMountainClicked(val mountainId: String) : MountainListEvent()
    object OnMapClicked : MountainListEvent()
    object OnFilterClicked : MountainListEvent()
    data class OnDifficultySelected(val difficulty: MountainDifficulty?) : MountainListEvent()
    data class OnElevationRangeChanged(val min: Int, val max: Int) : MountainListEvent()
    object OnClearFiltersClicked : MountainListEvent()
}

sealed class MountainListEffect : UiEffect {
    data class NavigateToDetail(val mountainId: String) : MountainListEffect()
    object NavigateToMap : MountainListEffect()
    data class ShowError(val message: String) : MountainListEffect()
}