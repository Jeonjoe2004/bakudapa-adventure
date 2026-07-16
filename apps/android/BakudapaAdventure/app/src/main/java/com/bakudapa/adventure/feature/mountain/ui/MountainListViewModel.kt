package com.bakudapa.adventure.feature.mountain.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.mountain.domain.model.Mountain
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDifficulty
import com.bakudapa.adventure.feature.mountain.domain.repository.MountainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MountainListViewModel @Inject constructor(
    private val repository: MountainRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MountainListState, MountainListEvent, MountainListEffect>(MountainListState()) {

    init {
        loadMountains()
        observeFilterChanges()
    }

    private fun loadMountains() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }

            launch {
                repository.getMountains().collectLatest { result ->
                    when (result) {
                        is DataResult.Success -> setState { it.copy(mountains = result.data, isLoading = false) }
                        is DataResult.Error -> setState { it.copy(error = result.exception.message, isLoading = false) }
                        DataResult.Loading -> {}
                    }
                }
            }
        }
    }

    private fun observeFilterChanges() {
        viewModelScope.launch {
            combine(
                uiState.map { it.mountains }.distinctUntilChanged(),
                uiState.map { it.searchQuery }.distinctUntilChanged(),
                uiState.map { it.selectedDifficulty }.distinctUntilChanged(),
                uiState.map { it.minElevation }.distinctUntilChanged(),
                uiState.map { it.maxElevation }.distinctUntilChanged(),
            ) { mountains, query, difficulty, minElev, maxElev ->
                mountains.filter { mountain ->
                    // Search filter
                    val matchesSearch = query.isBlank() ||
                        mountain.name.lowercase().contains(query.lowercase()) ||
                        mountain.location.lowercase().contains(query.lowercase())

                    // Difficulty filter
                    val matchesDifficulty = difficulty == null ||
                        mountain.difficulty == difficulty

                    // Elevation filter
                    val matchesElevation = (minElev == 0 || mountain.elevation >= minElev) &&
                        (maxElev == 0 || mountain.elevation <= maxElev)

                    matchesSearch && matchesDifficulty && matchesElevation
                }
            }.collectLatest { filtered ->
                setState { it.copy(filteredMountains = filtered) }
            }
        }
    }

    override fun onEvent(event: MountainListEvent) {
        when (event) {
            MountainListEvent.LoadMountains -> loadMountains()
            is MountainListEvent.OnSearchQueryChanged -> setState { it.copy(searchQuery = event.query) }
            MountainListEvent.OnSearchClicked -> {} // Trigger search
            is MountainListEvent.OnSearchActiveChanged -> setState { it.copy(isSearchActive = event.active) }
            MountainListEvent.OnMapClicked -> sendEffect(MountainListEffect.NavigateToMap)
            MountainListEvent.OnFilterClicked -> {} // Show filter bottom sheet
            is MountainListEvent.OnDifficultySelected -> setState { it.copy(selectedDifficulty = event.difficulty) }
            is MountainListEvent.OnElevationRangeChanged -> setState { it.copy(minElevation = event.min, maxElevation = event.max) }
            MountainListEvent.OnClearFiltersClicked -> setState { it.copy(selectedDifficulty = null, minElevation = 0, maxElevation = 0) }
            is MountainListEvent.OnMountainClicked -> sendEffect(MountainListEffect.NavigateToDetail(event.mountainId))
        }
    }
}