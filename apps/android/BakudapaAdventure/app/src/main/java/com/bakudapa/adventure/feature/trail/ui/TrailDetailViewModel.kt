package com.bakudapa.adventure.feature.trail.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import com.bakudapa.adventure.feature.trail.domain.repository.TrailRepository
import com.bakudapa.adventure.feature.trail.domain.repository.TrailReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailDetailViewModel @Inject constructor(
    private val repository: TrailRepository,
    private val reviewRepository: TrailReviewRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<TrailDetailState, TrailDetailEvent, TrailDetailEffect>(TrailDetailState()) {

    private val trailId: String = savedStateHandle.get<String>("trailId") ?: ""

    init {
        if (trailId.isNotBlank()) {
            loadTrail()
            loadReviews()
        }
    }

    override fun onEvent(event: TrailDetailEvent) {
        when (event) {
            TrailDetailEvent.LoadTrail -> loadTrail()
            TrailDetailEvent.OnStartTracking -> sendEffect(TrailDetailEffect.NavigateToTracking(trailId))
            is TrailDetailEvent.ReviewInputChanged -> setState { it.copy(reviewInput = event.text) }
            is TrailDetailEvent.ReviewRatingChanged -> setState { it.copy(reviewRating = event.rating) }
            TrailDetailEvent.ReviewSend -> sendReview()
        }
    }

    private fun loadTrail() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            repository.getTrailDetail(trailId).collectLatest { result ->
                when (result) {
                    is DataResult.Success -> {
                        setState { it.copy(trail = result.data, isLoading = false) }
                        if (result.data != null) loadAlternativeTrails(result.data.mountainId)
                    }
                    is DataResult.Error -> setState {
                        it.copy(error = result.exception.message, isLoading = false)
                    }
                    DataResult.Loading -> {}
                }
            }
        }
    }

    private fun loadAlternativeTrails(mountainId: String) {
        viewModelScope.launch {
            repository.getOtherTrails(mountainId, trailId).collectLatest { result ->
                if (result is DataResult.Success) {
                    setState { it.copy(alternativeTrails = result.data) }
                }
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            reviewRepository.getReviews(trailId).collectLatest { result ->
                if (result is DataResult.Success) {
                    setState { it.copy(reviews = result.data) }
                }
            }
        }
    }

    private fun sendReview() {
        val s = uiState.value
        if (s.reviewInput.isBlank()) return
        viewModelScope.launch {
            setState { it.copy(isSending = true) }
            when (reviewRepository.addReview(trailId, s.reviewRating, s.reviewInput)) {
                is DataResult.Success -> setState { it.copy(reviewInput = "", reviewRating = 0f, isSending = false) }
                is DataResult.Error -> setState { it.copy(isSending = false) }
                DataResult.Loading -> {}
            }
        }
    }
}
