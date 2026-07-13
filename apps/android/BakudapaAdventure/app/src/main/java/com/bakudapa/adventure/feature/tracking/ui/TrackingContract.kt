package com.bakudapa.adventure.feature.tracking.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus

data class TrackingState(
    val status: TrackingStatus = TrackingStatus.IDLE,
    val currentRoute: HikingRoute = HikingRoute(),
    val isSaving: Boolean = false,
    val error: String? = null
) : UiState

sealed class TrackingEvent : UiEvent {
    object StartTracking : TrackingEvent()
    object PauseTracking : TrackingEvent()
    object ResumeTracking : TrackingEvent()
    object StopTracking : TrackingEvent()
    data class SaveRoute(val name: String) : TrackingEvent()
    object ExportGPX : TrackingEvent()
}

sealed class TrackingEffect : UiEffect {
    object NavigateBack : TrackingEffect()
    data class ShowToast(val message: String) : TrackingEffect()
}
