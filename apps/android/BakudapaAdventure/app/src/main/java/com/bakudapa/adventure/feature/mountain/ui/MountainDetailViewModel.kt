package com.bakudapa.adventure.feature.mountain.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.mountain.domain.repository.MountainRepository
import com.bakudapa.adventure.feature.summit.domain.repository.SummitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MountainDetailViewModel @Inject constructor(
    private val repository: MountainRepository,
    private val summitRepository: SummitRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MountainDetailState, MountainDetailEvent, MountainDetailEffect>(MountainDetailState()) {

    private val mountainId: String = savedStateHandle["mountainId"] ?: ""

    init {
        if (mountainId.isNotBlank()) loadMountain()
    }

    override fun onEvent(event: MountainDetailEvent) {
        when (event) {
            MountainDetailEvent.LoadMountain -> loadMountain()
            is MountainDetailEvent.OnTrailClicked -> sendEffect(MountainDetailEffect.NavigateToTrail(event.trailId))
            MountainDetailEvent.OnOpenMapClicked -> {
                val m = uiState.value.mountain ?: return
                sendEffect(MountainDetailEffect.NavigateToMap(m.latitude, m.longitude, m.name))
            }
            MountainDetailEvent.OnOpenSummitLog -> {
                val m = uiState.value.mountain ?: return
                sendEffect(MountainDetailEffect.NavigateToCreateSummitLog(m.id, m.name))
            }
        }
    }

    private fun loadMountain() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }

            launch {
                repository.getMountainDetail(mountainId).collectLatest { result ->
                    when (result) {
                        is DataResult.Success -> {
                            setState { it.copy(mountain = result.data, isLoading = false) }
                            // Load weather setelah mountain detail siap
                            val m = result.data
                            if (m.latitude != 0.0) loadWeather(m.latitude, m.longitude)
                            loadSummitLogs()
                        }
                        is DataResult.Error -> setState { it.copy(error = result.exception.message, isLoading = false) }
                        DataResult.Loading -> {}
                    }
                }
            }

            launch {
                repository.getTrails(mountainId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(trails = result.data) }
                }
            }
        }
    }

    private fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.getWeather(lat, lon).collectLatest { result ->
                if (result is DataResult.Success) setState { it.copy(weather = result.data) }
            }
        }
    }

    private fun loadSummitLogs() {
        viewModelScope.launch {
            summitRepository.getSummitLogs(mountainId).collectLatest { result ->
                if (result is DataResult.Success) setState { it.copy(summitLogs = result.data) }
            }
        }
    }
}
