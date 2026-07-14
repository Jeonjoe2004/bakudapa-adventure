package com.bakudapa.adventure.feature.trail.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail

data class TrailDetailState(
    val isLoading: Boolean = false,
    val trail: TrailDetail? = null,
    val error: String? = null
) : UiState

sealed class TrailDetailEvent : UiEvent {
    object LoadTrail : TrailDetailEvent()
    object OnStartTracking : TrailDetailEvent()
}

sealed class TrailDetailEffect : UiEffect {
    data class NavigateToTracking(val trailId: String) : TrailDetailEffect()
    data class ShowError(val message: String) : TrailDetailEffect()
}
