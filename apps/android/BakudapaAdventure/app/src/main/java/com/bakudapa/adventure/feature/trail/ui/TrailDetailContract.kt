package com.bakudapa.adventure.feature.trail.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail
import com.bakudapa.adventure.feature.trail.domain.model.TrailReview

data class TrailDetailState(
    val isLoading: Boolean = false,
    val trail: TrailDetail? = null,
    val alternativeTrails: List<TrailInfo> = emptyList(),
    val error: String? = null,
    val reviews: List<TrailReview> = emptyList(),
    val reviewInput: String = "",
    val reviewRating: Float = 0f,
    val isSending: Boolean = false,
) : UiState

sealed class TrailDetailEvent : UiEvent {
    object LoadTrail : TrailDetailEvent()
    object OnStartTracking : TrailDetailEvent()
    data class ReviewInputChanged(val text: String) : TrailDetailEvent()
    data class ReviewRatingChanged(val rating: Float) : TrailDetailEvent()
    object ReviewSend : TrailDetailEvent()
}

sealed class TrailDetailEffect : UiEffect {
    data class NavigateToTracking(val trailId: String) : TrailDetailEffect()
    data class ShowError(val message: String) : TrailDetailEffect()
}
