package com.bakudapa.adventure.feature.mountain.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDetail
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo

data class MountainDetailState(
    val isLoading: Boolean = false,
    val mountain: MountainDetail? = null,
    val trails: List<TrailInfo> = emptyList(),
    val error: String? = null
) : UiState

sealed class MountainDetailEvent : UiEvent {
    object LoadMountain : MountainDetailEvent()
    data class OnTrailClicked(val trailId: String) : MountainDetailEvent()
    object OnOpenMapClicked : MountainDetailEvent()
}

sealed class MountainDetailEffect : UiEffect {
    data class NavigateToTrail(val id: String) : MountainDetailEffect()
    data class NavigateToMap(val lat: Double, val lng: Double, val name: String) : MountainDetailEffect()
    data class ShowError(val message: String) : MountainDetailEffect()
}
