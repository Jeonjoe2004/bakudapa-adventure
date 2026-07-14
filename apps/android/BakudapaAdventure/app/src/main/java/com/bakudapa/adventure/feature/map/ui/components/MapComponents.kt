package com.bakudapa.adventure.feature.map.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style

@Composable
fun MountainMap(
    markers: List<MapMarker>,
    userLocation: Pair<Double, Double>?,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    
    // Initialize MapLibre if not already initialized
    LaunchedEffect(Unit) {
        if (!MapLibre.isInitialized()) {
            MapLibre.getInstance(context)
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    getMapAsync(OnMapReadyCallback { map ->
                        maplibreMap = map
                        map.setStyle(Style.getPredefinedStyle("Outdoor")) { style ->
                            // Clear existing markers
                            map.markers?.forEach { map.removeMarker(it) }
                            
                            // Add markers from data
                            markers.forEach { marker ->
                                val markerOptions = org.maplibre.android.annotations.MarkerOptions()
                                    .position(LatLng(marker.latitude, marker.longitude))
                                    .title(marker.title)
                                    .snippet(marker.description)
                                
                                val mapMarker = map.addMarker(markerOptions)
                                mapMarker.tag = marker.id // Store marker ID for click handling
                            }
                            
                            // Set camera position
                            val targetLatLng = if (markers.isNotEmpty()) {
                                LatLng(markers.first().latitude, markers.first().longitude)
                            } else {
                                LatLng(1.45, 125.0) // Default to North Sulawesi
                            }
                            
                            map.cameraPosition = CameraPosition.Builder()
                                .target(targetLatLng)
                                .zoom(10.0)
                                .build()
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                // Update markers if markers list changes
                maplibreMap?.let { map ->
                    map.setStyle(Style.getPredefinedStyle("Outdoor")) { style ->
                        // Clear existing markers
                        map.markers?.forEach { map.removeMarker(it) }
                        
                        // Add new markers
                        markers.forEach { marker ->
                            val markerOptions = org.maplibre.android.annotations.MarkerOptions()
                                .position(LatLng(marker.latitude, marker.longitude))
                                .title(marker.title)
                                .snippet(marker.description)
                            
                            val mapMarker = map.addMarker(markerOptions)
                            mapMarker.tag = marker.id
                        }
                    }
                }
            }
        )

        // Map Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { 
                    maplibreMap?.let { map ->
                        userLocation?.let { (lat, lng) ->
                            map.animateCamera(
                                CameraPosition.Builder()
                                    .target(LatLng(lat, lng))
                                    .zoom(14.0)
                                    .build()
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }
    }
}
