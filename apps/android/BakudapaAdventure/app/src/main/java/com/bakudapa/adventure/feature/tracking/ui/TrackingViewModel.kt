package com.bakudapa.adventure.feature.tracking.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.tracking.domain.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: TrackingRepository
) : BaseViewModel<TrackingState, TrackingEvent, TrackingEffect>(TrackingState()) {

    init {
        observeTracking()
    }

    private fun observeTracking() {
        viewModelScope.launch {
            combine(
                repository.trackingStatus,
                repository.currentRoute
            ) { status, route ->
                setState { it.copy(status = status, currentRoute = route) }
            }.collect {}
        }
    }

    override fun onEvent(event: TrackingEvent) {
        when (event) {
            TrackingEvent.StartTracking -> viewModelScope.launch { repository.startTracking() }
            TrackingEvent.PauseTracking -> viewModelScope.launch { repository.pauseTracking() }
            TrackingEvent.ResumeTracking -> viewModelScope.launch { repository.resumeTracking() }
            TrackingEvent.StopTracking -> viewModelScope.launch { repository.stopTracking() }
            is TrackingEvent.SaveRoute -> saveRoute(event.name)
            TrackingEvent.ExportGPX -> exportGPX()
        }
    }

    private fun saveRoute(name: String) {
        viewModelScope.launch {
            setState { it.copy(isSaving = true) }
            val route = uiState.value.currentRoute.copy(name = name)
            when (val result = repository.saveRoute(route)) {
                is DataResult.Success -> {
                    setState { it.copy(isSaving = false) }
                    sendEffect(TrackingEffect.ShowToast("Route saved!"))
                    sendEffect(TrackingEffect.NavigateBack)
                }
                is DataResult.Error -> {
                    setState { it.copy(isSaving = false, error = result.exception.message) }
                }
                DataResult.Loading -> {}
            }
        }
    }

    private fun exportGPX() {
        viewModelScope.launch {
            val route = uiState.value.currentRoute
            when (val result = repository.exportToGpx(route)) {
                is DataResult.Success -> sendEffect(TrackingEffect.ShowToast("GPX exported to: ${result.data}"))
                is DataResult.Error -> sendEffect(TrackingEffect.ShowToast("Export failed: ${result.exception.message}"))
                DataResult.Loading -> {}
            }
        }
    }
}
