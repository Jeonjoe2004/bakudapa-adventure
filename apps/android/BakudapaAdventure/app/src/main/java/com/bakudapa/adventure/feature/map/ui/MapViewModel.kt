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
            MapEvent.OnDownloadMapClicked -> {
                downloadMapRegion("current_area")
            }
            is MapEvent.OnDownloadRegion -> {
                downloadMapRegion(event.regionName)
            }
            MapEvent.OnToggleOfflineMode -> {
                val newMode = !uiState.value.isOfflineMode
                setState { it.copy(isOfflineMode = newMode) }
                sendEffect(
                    MapEffect.ShowToast(
                        if (newMode) "Offline mode ON" else "Offline mode OFF"
                    )
                )
            }
            MapEvent.OnToggleCompass -> {
                setState { it.copy(isCompassEnabled = !it.isCompassEnabled) }
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

    private fun downloadMapRegion(regionName: String) {
        viewModelScope.launch {
            setState { it.copy(isDownloading = true, downloadProgress = 0) }
            // Simulate progress while download happens
            for (p in 0..100 step 10) {
                setState { it.copy(downloadProgress = p) }
                kotlinx.coroutines.delay(150)
            }
            val result = mapRepository.downloadMapRegion(regionName)
            when (result) {
                is DataResult.Success -> {
                    setState {
                        it.copy(
                            isDownloading = false,
                            downloadProgress = 100,
                            downloadedRegions = it.downloadedRegions + regionName
                        )
                    }
                    sendEffect(MapEffect.ShowToast("Map region '$regionName' downloaded"))
                }
                is DataResult.Error -> {
                    setState { it.copy(isDownloading = false) }
                    sendEffect(MapEffect.ShowToast("Download failed: ${result.exception.message}"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
