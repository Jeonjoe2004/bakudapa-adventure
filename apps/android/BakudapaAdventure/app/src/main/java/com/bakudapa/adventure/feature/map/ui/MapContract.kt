package com.bakudapa.adventure.feature.map.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.map.domain.model.MapMarker

data class MapState(
    val isLoading: Boolean = false,
    val markers: List<MapMarker> = emptyList(),
    val userLocation: Pair<Double, Double>? = null,
    val zoomLevel: Double = 10.0,
    val isCompassEnabled: Boolean = true,
    val compassAzimuth: Float = 0f,
    val isOfflineMode: Boolean = false,
    val downloadedRegions: List<String> = emptyList(),
    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0
) : UiState

sealed class MapEvent : UiEvent {
    object LoadMarkers : MapEvent()
    data class OnMarkerClicked(val markerId: String) : MapEvent()
    data class OnLocationChanged(val lat: Double, val lng: Double) : MapEvent()
    data class OnCompassChanged(val azimuth: Float) : MapEvent()
    object OnDownloadMapClicked : MapEvent()
    data class OnDownloadRegion(val regionName: String) : MapEvent()
    object OnToggleOfflineMode : MapEvent()
    object OnToggleCompass : MapEvent()
}

sealed class MapEffect : UiEffect {
    data class NavigateToDetails(val markerId: String) : MapEffect()
    data class ShowToast(val message: String) : MapEffect()
}
