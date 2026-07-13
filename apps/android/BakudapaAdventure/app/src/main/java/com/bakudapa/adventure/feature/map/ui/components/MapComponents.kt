package com.bakudapa.adventure.feature.map.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bakudapa.adventure.feature.map.domain.model.MapMarker
import org.maplibre.gl.MapLibre
import org.maplibre.gl.camera.CameraPosition
import org.maplibre.gl.camera.CameraUpdateFactory
import org.maplibre.gl.geometry.LatLng
import org.maplibre.gl.maps.MapView
import org.maplibre.gl.maps.Style

@Composable
fun MountainMap(
    markers: List<MapMarker>,
    userLocation: Pair<Double, Double>?,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    getMapAsync { map ->
                        map.setStyle(Style.getPredefinedStyle("Outdoor"))
                        
                        // Set initial position
                        map.cameraPosition = CameraPosition.Builder()
                            .target(LatLng(1.45, 125.0))
                            .zoom(10.0)
                            .build()
                        
                        // Add markers
                        markers.forEach { marker ->
                            map.addMarker(
                                org.maplibre.gl.annotations.MarkerOptions()
                                    .position(LatLng(marker.latitude, marker.longitude))
                                    .title(marker.title)
                            )
                        }
                    }
                    mapView = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                // Update markers or camera if needed
            }
        )

        // Map Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { /* TODO: Center on user */ },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
