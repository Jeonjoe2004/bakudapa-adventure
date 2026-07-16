package com.bakudapa.adventure.feature.home.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.home.domain.model.HomeData
import com.bakudapa.adventure.feature.home.domain.model.Mountain

data class HomeState(
    val isLoading: Boolean = false,
    val homeData: HomeData? = null,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResults: List<Mountain> = emptyList(),
    val isSearching: Boolean = false
) : UiState

sealed class HomeEvent : UiEvent {
    object LoadHomeData : HomeEvent()
    data class OnSearchQueryChanged(val query: String) : HomeEvent()
    object OnSearchClicked : HomeEvent()
    data class OnMountainClicked(val mountainId: String) : HomeEvent()
    data class OnTrailClicked(val trailId: String) : HomeEvent()
    data class OnPostClicked(val postId: String) : HomeEvent()
    object OnViewAllMountainsClicked : HomeEvent()
}

sealed class HomeEffect : UiEffect {
    data class NavigateToMountainDetail(val id: String) : HomeEffect()
    data class NavigateToTrailDetail(val id: String) : HomeEffect()
    data class NavigateToPostDetail(val id: String) : HomeEffect()
    object NavigateToMountainList : HomeEffect()
    data class ShowError(val message: String) : HomeEffect()
}
