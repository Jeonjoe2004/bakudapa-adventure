package com.bakudapa.adventure.feature.mountain.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.home.domain.model.Weather
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDetail
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import com.bakudapa.adventure.feature.summit.domain.model.SummitLog

data class MountainDetailState(
    val isLoading: Boolean = false,
    val mountain: MountainDetail? = null,
    val trails: List<TrailInfo> = emptyList(),
    val weather: Weather? = null,
    val summitLogs: List<SummitLog> = emptyList(),
    val error: String? = null
) : UiState

sealed class MountainDetailEvent : UiEvent {
    object LoadMountain : MountainDetailEvent()
    data class OnTrailClicked(val trailId: String) : MountainDetailEvent()
    object OnOpenMapClicked : MountainDetailEvent()
    object OnOpenSummitLog : MountainDetailEvent()
}

sealed class MountainDetailEffect : UiEffect {
    data class NavigateToTrail(val id: String) : MountainDetailEffect()
    data class NavigateToMap(val lat: Double, val lng: Double, val name: String) : MountainDetailEffect()
    data class NavigateToCreateSummitLog(val mountainId: String, val mountainName: String) : MountainDetailEffect()
    data class ShowError(val message: String) : MountainDetailEffect()
}
