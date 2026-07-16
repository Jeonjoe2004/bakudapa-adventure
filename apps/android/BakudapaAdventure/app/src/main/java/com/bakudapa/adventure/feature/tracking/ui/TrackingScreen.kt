package com.bakudapa.adventure.feature.tracking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.R
import com.bakudapa.adventure.core.ui.components.HandlePermissions
import com.bakudapa.adventure.core.utils.PermissionUtils
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import kotlinx.coroutines.flow.collectLatest
import com.bakudapa.adventure.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit = {},
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandlePermissions(
        permissions = PermissionUtils.locationPermissions.toList(),
        rationaleMessage = stringResource(R.string.permission_location_rationale),
        onPermissionGranted = {
            TrackingContent(
                state = state,
                snackbarHostState = snackbarHostState,
                onNavigateBack = onNavigateBack,
                onNavigateToCreatePost = onNavigateToCreatePost,
                viewModel = viewModel
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackingContent(
    state: TrackingState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit,
    viewModel: TrackingViewModel
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var routeName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                TrackingEffect.NavigateBack -> onNavigateBack()
                is TrackingEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LaunchedEffect(state.status) {
        if (state.status == TrackingStatus.STOP && state.currentRoute.points.isNotEmpty()) {
            showSaveDialog = true
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Simpan Rute") },
            text = {
                Column {
                    Text(
                        "Selesai! Rute tercatat %.2f km".format(state.currentRoute.distanceMeters / 1000),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = routeName,
                        onValueChange = { routeName = it },
                        label = { Text("Nama Rute") },
                        placeholder = { Text("Contoh: Pendakian Rinjani") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = routeName.ifBlank { "Rute ${System.currentTimeMillis()}" }
                        viewModel.onEvent(TrackingEvent.SaveRoute(name))
                        showSaveDialog = false
                    },
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                Row {
                    if (routeName.isNotBlank()) {
                        TextButton(onClick = {
                            onNavigateToCreatePost("✅ Pendakian selesai: $routeName - ${"%.2f".format(state.currentRoute.distanceMeters / 1000)}km, ${state.currentRoute.calories}kkal")
                            showSaveDialog = false
                        }) {
                            Text("Simpan & Bagikan", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Buang")
                    }
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Hiking Tracker") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (state.status != TrackingStatus.IDLE) {
                        IconButton(onClick = { viewModel.onEvent(TrackingEvent.ExportGPX) }) {
                            Icon(Icons.Default.Share, contentDescription = "Export GPX")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            TrackingStatusChip(status = state.status)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    label = "Jarak",
                    value = "%.2f".format(state.currentRoute.distanceMeters / 1000),
                    unit = "km",
                    icon = Icons.Default.Place,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Waktu",
                    value = formatDuration(state.currentRoute.durationMillis),
                    unit = "",
                    icon = Icons.Default.Timer,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    label = "Elevasi",
                    value = "${state.currentRoute.maxElevation.toInt()}",
                    unit = "m",
                    icon = Icons.Default.Terrain,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Kecepatan",
                    value = "%.1f".format(state.currentRoute.avgSpeed * 3.6f),
                    unit = "km/j",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    label = "Kalori",
                    value = "${state.currentRoute.calories}",
                    unit = "kkal",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Titik GPS",
                    value = "${state.currentRoute.points.size}",
                    unit = "titik",
                    icon = Icons.Default.GpsFixed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TrackingControls(
                status = state.status,
                onStart = { viewModel.onEvent(TrackingEvent.StartTracking) },
                onPause = { viewModel.onEvent(TrackingEvent.PauseTracking) },
                onResume = { viewModel.onEvent(TrackingEvent.ResumeTracking) },
                onStop = { viewModel.onEvent(TrackingEvent.StopTracking) }
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun TrackingStatusChip(status: TrackingStatus) {
    val (label, color) = when (status) {
        TrackingStatus.IDLE  -> "Siap Mulai" to MaterialTheme.colorScheme.outline
        TrackingStatus.START -> "Sedang Tracking" to Color(0xFF4CAF50)
        TrackingStatus.PAUSE -> "Dijeda" to Color(0xFFFFC107)
        TrackingStatus.STOP  -> "Selesai" to MaterialTheme.colorScheme.error
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status == TrackingStatus.START) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(label, color = color, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun TrackingControls(
    status: TrackingStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            TrackingStatus.IDLE -> {
                TrackingFab(
                    icon = Icons.Default.PlayArrow,
                    label = "Mulai",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onStart,
                    size = 80
                )
            }
            TrackingStatus.START -> {
                TrackingFab(
                    icon = Icons.Default.Pause,
                    label = "Jeda",
                    color = Color(0xFFFFC107),
                    onClick = onPause,
                    size = 64
                )
                Spacer(modifier = Modifier.width(32.dp))
                TrackingFab(
                    icon = Icons.Default.Stop,
                    label = "Selesai",
                    color = MaterialTheme.colorScheme.error,
                    onClick = onStop,
                    size = 64
                )
            }
            TrackingStatus.PAUSE -> {
                TrackingFab(
                    icon = Icons.Default.PlayArrow,
                    label = "Lanjut",
                    color = Color(0xFF4CAF50),
                    onClick = onResume,
                    size = 64
                )
                Spacer(modifier = Modifier.width(32.dp))
                TrackingFab(
                    icon = Icons.Default.Stop,
                    label = "Selesai",
                    color = MaterialTheme.colorScheme.error,
                    onClick = onStop,
                    size = 64
                )
            }
            TrackingStatus.STOP -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun TrackingFab(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    size: Int = 64
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.size(size.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size((size * 0.45f).dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0L) return "00:00:00"
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours   = (millis / (1000 * 60 * 60))
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
