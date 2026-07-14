package com.bakudapa.adventure.feature.map.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
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
            MapEvent.OnDownloadMapClicked -> {
                // TODO: Implement offline map download
                sendEffect(MapEffect.ShowToast("Downloading map region..."))
            }
            MapEvent.OnToggleOfflineMode -> {
                setState { it.copy(isOfflineMode = !it.isOfflineMode) }
            }
        }
    }

    private fun loadMarkers() {
        viewModelScope.launch {
            mapRepository.getMapMarkers().collect { result ->
                when (result) {
                    is DataResult.Success -> setState { it.copy(markers = result.data, isLoading = false) }
                    is DataResult.Error -> setState { it.copy(isLoading = false) }
                    DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }
}
