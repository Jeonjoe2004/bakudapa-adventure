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
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.camera.CameraUpdateFactory

@Composable
fun MountainMap(
    markers: List<MapMarker>,
    userLocation: Pair<Double, Double>?,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markerMap = remember { mutableMapOf<String, String>() }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).also { mapView = it }.apply {
                    getMapAsync { map ->
                        map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) {
                            // Populate
                        }
                        markers.forEach { marker ->
                            map.addMarker(
                                org.maplibre.android.annotations.MarkerOptions()
                                    .position(LatLng(marker.latitude, marker.longitude))
                                    .title(marker.title)
                                    .snippet(marker.description)
                            )
                            markerMap[marker.title] = marker.id
                        }
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(markers.firstOrNull()?.latitude ?: 1.45, markers.firstOrNull()?.longitude ?: 125.0),
                            9.0
                        ))
                        map.setOnMarkerClickListener { clickedMarker ->
                            val id = markerMap[clickedMarker.title]
                            if (id != null) onMarkerClick(id)
                            true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.getMapAsync { map ->
                    map.removeAnnotations()
                    markers.forEach { marker ->
                        map.addMarker(
                            org.maplibre.android.annotations.MarkerOptions()
                                .position(LatLng(marker.latitude, marker.longitude))
                                .title(marker.title)
                                .snippet(marker.description)
                        )
                        markerMap[marker.title] = marker.id
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    userLocation?.let { (lat, lng) ->
                        mapView?.getMapAsync { map ->
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 14.0))
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
