package com.bakudapa.adventure.feature.home.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.home.domain.usecase.GetHomeDataUseCase
import com.bakudapa.adventure.feature.home.domain.usecase.SearchMountainsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val searchMountainsUseCase: SearchMountainsUseCase
) : BaseViewModel<HomeState, HomeEvent, HomeEffect>(HomeState()) {

    private var searchJob: Job? = null

    init {
        loadHomeData()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadHomeData -> loadHomeData()
            is HomeEvent.OnSearchQueryChanged -> {
                setState { it.copy(searchQuery = event.query) }
                debounceSearch(event.query)
            }
            HomeEvent.OnSearchClicked -> performSearch()
            is HomeEvent.OnMountainClicked -> {
                sendEffect(HomeEffect.NavigateToMountainDetail(event.mountainId))
            }
            is HomeEvent.OnTrailClicked -> {
                sendEffect(HomeEffect.NavigateToTrailDetail(event.trailId))
            }
            is HomeEvent.OnPostClicked -> {
                sendEffect(HomeEffect.NavigateToPostDetail(event.postId))
            }
            HomeEvent.OnViewAllMountainsClicked -> {
                sendEffect(HomeEffect.NavigateToMountainList)
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true, error = null) }
            getHomeDataUseCase().collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        setState { it.copy(isLoading = false, homeData = result.data) }
                    }
                    is DataResult.Error -> {
                        setState { it.copy(isLoading = false, error = result.exception.message) }
                        sendEffect(HomeEffect.ShowError(result.exception.message ?: "Failed to load data"))
                    }
                    DataResult.Loading -> {}
                }
            }
        }
    }

    private fun debounceSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            setState { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch()
        }
    }

    private fun performSearch() {
        val query = uiState.value.searchQuery
        if (query.isBlank()) return
        viewModelScope.launch {
            setState { it.copy(isSearching = true) }
            when (val result = searchMountainsUseCase(query)) {
                is DataResult.Success -> {
                    setState { it.copy(searchResults = result.data, isSearching = false) }
                }
                is DataResult.Error -> {
                    setState { it.copy(isSearching = false) }
                    sendEffect(HomeEffect.ShowError(result.exception.message ?: "Search failed"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}