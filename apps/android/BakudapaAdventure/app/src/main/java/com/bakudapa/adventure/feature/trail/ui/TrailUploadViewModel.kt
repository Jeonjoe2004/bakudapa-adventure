package com.bakudapa.adventure.feature.trail.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.trail.domain.model.PointOfInterest
import com.bakudapa.adventure.feature.trail.domain.model.TrailUpload
import com.bakudapa.adventure.feature.trail.domain.repository.TrailUploadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrailUploadState(
    val name: String = "",
    val mountainName: String = "",
    val difficulty: String = "MODERATE",
    val durationMinutes: Int = 0,
    val distanceKm: Double = 0.0,
    val description: String = "",
    val elevationGain: Int = 0,
    val maxElevation: Int = 0,
    val pois: List<PointOfInterest> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
) : UiState

sealed class TrailUploadEvent : UiEvent {
    data class NameChanged(val v: String) : TrailUploadEvent()
    data class MountainNameChanged(val v: String) : TrailUploadEvent()
    data class DifficultyChanged(val v: String) : TrailUploadEvent()
    data class DurationChanged(val v: Int) : TrailUploadEvent()
    data class DistanceChanged(val v: Double) : TrailUploadEvent()
    data class DescriptionChanged(val v: String) : TrailUploadEvent()
    data class AddPoi(val poi: PointOfInterest) : TrailUploadEvent()
    data class RemovePoi(val poi: PointOfInterest) : TrailUploadEvent()
    object Submit : TrailUploadEvent()
}

sealed class TrailUploadEffect : UiEffect {
    data class ShowError(val msg: String) : TrailUploadEffect()
}

@HiltViewModel
class TrailUploadViewModel @Inject constructor(
    private val repository: TrailUploadRepository
) : BaseViewModel<TrailUploadState, TrailUploadEvent, TrailUploadEffect>(TrailUploadState()) {

    override fun onEvent(event: TrailUploadEvent) {
        when (event) {
            is TrailUploadEvent.NameChanged -> setState { it.copy(name = event.v) }
            is TrailUploadEvent.MountainNameChanged -> setState { it.copy(mountainName = event.v) }
            is TrailUploadEvent.DifficultyChanged -> setState { it.copy(difficulty = event.v) }
            is TrailUploadEvent.DurationChanged -> setState { it.copy(durationMinutes = event.v) }
            is TrailUploadEvent.DistanceChanged -> setState { it.copy(distanceKm = event.v) }
            is TrailUploadEvent.DescriptionChanged -> setState { it.copy(description = event.v) }
            is TrailUploadEvent.AddPoi -> setState { it.copy(pois = it.pois + event.poi) }
            is TrailUploadEvent.RemovePoi -> setState { it.copy(pois = it.pois - event.poi) }
            TrailUploadEvent.Submit -> submit()
        }
    }

    private fun submit() {
        val s = uiState.value
        if (s.name.isBlank()) return
        viewModelScope.launch {
            setState { it.copy(isSaving = true) }
            val trail = TrailUpload(
                name = s.name,
                mountainName = s.mountainName,
                difficulty = s.difficulty,
                durationMinutes = s.durationMinutes,
                distanceKm = s.distanceKm,
                description = s.description,
                elevationGain = s.elevationGain,
                maxElevation = s.maxElevation,
                pointsOfInterest = s.pois,
                status = "pending",
            )
            val result = repository.uploadTrail(trail)
            if (result is DataResult.Success) {
                setState { it.copy(isSaving = false, saved = true) }
            } else if (result is DataResult.Error) {
                setState { it.copy(isSaving = false) }
                sendEffect(TrailUploadEffect.ShowError("Failed to upload trail"))
            }
        }
    }
}
