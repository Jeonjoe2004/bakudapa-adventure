package com.bakudapa.adventure.feature.trail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.trail.domain.repository.TrailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailDetailViewModel @Inject constructor(
    private val repository: TrailRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<TrailDetailState, TrailDetailEvent, TrailDetailEffect>(TrailDetailState()) {

    private val trailId: String = savedStateHandle.get<String>("trailId") ?: ""

    init {
        if (trailId.isNotBlank()) loadTrail()
    }

    override fun onEvent(event: TrailDetailEvent) {
        when (event) {
            TrailDetailEvent.LoadTrail -> loadTrail()
            TrailDetailEvent.OnStartTracking -> sendEffect(TrailDetailEffect.NavigateToTracking(trailId))
        }
    }

    private fun loadTrail() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            repository.getTrailDetail(trailId).collectLatest { result ->
                when (result) {
                    is DataResult.Success -> setState { it.copy(trail = result.data, isLoading = false) }
                    is DataResult.Error -> setState {
                        it.copy(error = result.exception.message, isLoading = false)
                    }
                    DataResult.Loading -> {}
                }
            }
        }
    }
}
