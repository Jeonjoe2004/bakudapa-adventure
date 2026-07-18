package com.bakudapa.adventure.feature.map.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.map.domain.model.MapMarker

data class MapState(
    val isLoading: Boolean = false,
    val markers: List<MapMarker> = emptyList(),
    val userLocation: Pair<Double, Double>? = null,
    val userBearing: Float = 0f,
    val isFollowing: Boolean = false,
    val waypoints: List<Pair<Double, Double>> = emptyList(),
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
    object OnToggleOfflineMode : MapEvent()
    object OnToggleCompass : MapEvent()
    object OnToggleFollow : MapEvent()
    data class OnMapTapped(val lat: Double, val lng: Double) : MapEvent()
    data class OnAddWaypoint(val lat: Double, val lng: Double) : MapEvent()
    object OnClearWaypoints : MapEvent()
    data class OnRouteToSummit(val mountainLat: Double, val mountainLng: Double) : MapEvent()
}

sealed class MapEffect : UiEffect {
    data class NavigateToDetails(val markerId: String) : MapEffect()
    data class ShowToast(val message: String) : MapEffect()
}
