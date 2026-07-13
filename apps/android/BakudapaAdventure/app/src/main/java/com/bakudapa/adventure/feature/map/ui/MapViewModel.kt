package com.bakudapa.adventure.feature.map.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.feature.map.domain.model.MarkerType
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : BaseViewModel<MapState, MapEvent, MapEffect>(MapState()) {

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
            setState { it.copy(isLoading = true) }
            // Mock markers for now
            val mockMarkers = listOf(
                MapMarker("1", "Klabat Summit", "Highest peak in North Sulawesi", 1.45, 125.0, MarkerType.SUMMIT, 1995),
                MapMarker("2", "Lokon Crater", "Active volcano crater", 1.35, 124.8, MarkerType.DANGER_ZONE, 1580),
                MapMarker("3", "Mahawu Camping", "Perfect spot for sunrise", 1.36, 124.9, MarkerType.CAMPING_GROUND, 1324)
            )
            setState { it.copy(markers = mockMarkers, isLoading = false) }
        }
    }
}
