package com.bakudapa.adventure.feature.map.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.R
import com.bakudapa.adventure.core.ui.components.HandlePermissions
import com.bakudapa.adventure.core.utils.PermissionUtils
import com.bakudapa.adventure.feature.map.ui.components.MountainMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.NavigateToDetails -> { /* handle via parent if needed */ }
                is MapEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // Compass sensor listener
    DisposableEffect(state.isCompassEnabled) {
        if (!state.isCompassEnabled) {
            onDispose { }
            return@DisposableEffect onDispose { }
        }
        val sensorManager = context.getSystemService<SensorManager>()
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensorManager == null || rotationSensor == null) {
            onDispose { }
            return@DisposableEffect onDispose { }
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    val orientationValues = FloatArray(3)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationValues)
                    val azimuthDegrees = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
                    viewModel.onEvent(MapEvent.OnCompassChanged(azimuthDegrees))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    HandlePermissions(
        permissions = PermissionUtils.locationPermissions.toList(),
        rationaleMessage = stringResource(R.string.permission_location_rationale),
        onPermissionGranted = {
            MapContent(
                state = state,
                snackbarHostState = snackbarHostState,
                onNavigateBack = onNavigateBack,
                viewModel = viewModel
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapContent(
    state: MapState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel
) {
    var showDownloadSheet by remember { mutableStateOf(false) }

    if (showDownloadSheet) {
        DownloadRegionSheet(
            downloadedRegions = state.downloadedRegions,
            isDownloading = state.isDownloading,
            progress = state.downloadProgress,
            onDismiss = { showDownloadSheet = false },
            onDownload = { name -> viewModel.onEvent(MapEvent.OnDownloadRegion(name)) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mountain Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDownloadSheet = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Download Area")
                    }
                    IconButton(onClick = { viewModel.onEvent(MapEvent.OnToggleOfflineMode) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Map Settings",
                            tint = if (state.isOfflineMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }

            MountainMap(
                markers = state.markers,
                userLocation = state.userLocation,
                onMarkerClick = { viewModel.onEvent(MapEvent.OnMarkerClicked(it)) },
                modifier = Modifier.fillMaxSize()
            )

            if (state.isCompassEnabled) {
                CompassOverlay(
                    azimuth = state.compassAzimuth,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }

            if (state.isDownloading) {
                LinearProgressIndicator(
                    progress = { state.downloadProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun CompassOverlay(
    azimuth: Float,
    modifier: Modifier = Modifier
) {
    // Pointing north: rotate opposite of azimuth
    val rotation by animateFloatAsState(targetValue = -azimuth, label = "compass")
    Surface(
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Explore,
                contentDescription = "Compass",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotation }
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${azimuth.toInt().let { if (it < 0) it + 360 else it }}°",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DownloadRegionSheet(
    downloadedRegions: List<String>,
    isDownloading: Boolean,
    progress: Int,
    onDismiss: () -> Unit,
    onDownload: (String) -> Unit
) {
    var regionName by remember { mutableStateOf("Sulawesi Utara") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Offline Maps", style = MaterialTheme.typography.titleLarge)
            Text(
                "Download peta untuk akses offline saat mendaki tanpa sinyal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            OutlinedTextField(
                value = regionName,
                onValueChange = { regionName = it },
                label = { Text("Nama Region") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (isDownloading) {
                Column {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("$progress%", style = MaterialTheme.typography.labelSmall)
                }
            }

            Button(
                onClick = { onDownload(regionName.ifBlank { "current_area" }) },
                enabled = !isDownloading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Download, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (isDownloading) "Downloading..." else "Download")
            }

            if (downloadedRegions.isNotEmpty()) {
                HorizontalDivider()
                Text("Downloaded:", style = MaterialTheme.typography.labelLarge)
                downloadedRegions.forEach { r ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("• $r", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Text("✓", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
