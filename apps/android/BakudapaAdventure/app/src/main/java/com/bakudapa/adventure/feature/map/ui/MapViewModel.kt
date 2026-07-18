package com.bakudapa.adventure.feature.map.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.domain.repository.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : BaseViewModel<MapState, MapEvent, MapEffect>(MapState()) {

    init {
        loadMarkers()
    }

    override fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.LoadMarkers -> loadMarkers()
            is MapEvent.OnMarkerClicked -> {
                sendEffect(MapEffect.NavigateToDetails(event.markerId))
            }
            is MapEvent.OnLocationChanged -> {
                setState { it.copy(userLocation = Pair(event.lat, event.lng)) }
            }
            is MapEvent.OnCompassChanged -> {
                setState { it.copy(compassAzimuth = event.azimuth) }
            }
            MapEvent.OnToggleOfflineMode -> {
                val newMode = !uiState.value.isOfflineMode
                setState { it.copy(isOfflineMode = newMode) }
                sendEffect(MapEffect.ShowToast(if (newMode) "Offline mode ON" else "Offline mode OFF"))
            }
            MapEvent.OnToggleCompass -> {
                setState { it.copy(isCompassEnabled = !it.isCompassEnabled) }
            }
            MapEvent.OnToggleFollow -> {
                setState { it.copy(isFollowing = !it.isFollowing) }
            }
            is MapEvent.OnMapTapped -> {
                setState { it.copy(waypoints = it.waypoints + Pair(event.lat, event.lng)) }
            }
            is MapEvent.OnAddWaypoint -> {
                setState { it.copy(waypoints = it.waypoints + Pair(event.lat, event.lng)) }
            }
            MapEvent.OnClearWaypoints -> {
                setState { it.copy(waypoints = emptyList()) }
            }
            is MapEvent.OnRouteToSummit -> {
                sendEffect(MapEffect.ShowToast("Route to summit coming soon"))
            }
        }
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            mapRepository.getMapMarkers().collect { result ->
                when (result) {
                    is DataResult.Success -> setState { it.copy(markers = result.data, isLoading = false) }
                    is DataResult.Error -> {
                        setState { it.copy(isLoading = false) }
                        sendEffect(MapEffect.ShowToast(result.exception.message ?: "Failed to load markers"))
                    }
                    DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }
}