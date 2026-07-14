package com.bakudapa.adventure.feature.mountain.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.mountain.domain.repository.MountainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MountainDetailViewModel @Inject constructor(
    private val repository: MountainRepository,
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
        }
    }

    private fun loadMountain() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }

            launch {
                repository.getMountainDetail(mountainId).collectLatest { result ->
                    when (result) {
                        is DataResult.Success -> setState { it.copy(mountain = result.data, isLoading = false) }
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
}
