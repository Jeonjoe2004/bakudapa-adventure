package com.bakudapa.adventure.feature.tracking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.tracking.domain.model.TrackingStatus
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                TrackingEffect.NavigateBack -> onNavigateBack()
                is TrackingEffect.ShowToast -> { /* Show snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hiking Tracker") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Metrics Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(label = "Distance", value = "%.2f".format(state.currentRoute.distanceMeters / 1000), unit = "km", modifier = Modifier.weight(1f))
                MetricItem(label = "Duration", value = formatDuration(state.currentRoute.durationMillis), unit = "", modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricItem(label = "Elevation", value = "${state.currentRoute.maxElevation.toInt()}", unit = "m", modifier = Modifier.weight(1f))
                MetricItem(label = "Speed", value = "%.1f".format(state.currentRoute.avgSpeed * 3.6), unit = "km/h", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tracking Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.status == TrackingStatus.START) {
                    TrackingButton(icon = Icons.Default.Pause, color = Color.Yellow, onClick = { viewModel.onEvent(TrackingEvent.PauseTracking) })
                    Spacer(modifier = Modifier.width(32.dp))
                    TrackingButton(icon = Icons.Default.Stop, color = Color.Red, onClick = { viewModel.onEvent(TrackingEvent.StopTracking) })
                } else if (state.status == TrackingStatus.PAUSE || state.status == TrackingStatus.IDLE) {
                    TrackingButton(
                        icon = Icons.Default.PlayArrow,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { 
                            if (state.status == TrackingStatus.IDLE) viewModel.onEvent(TrackingEvent.StartTracking)
                            else viewModel.onEvent(TrackingEvent.ResumeTracking)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = unit, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 6.dp))
            }
        }
    }
}

@Composable
fun TrackingButton(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    LargeFloatingActionButton(
        onClick = onClick,
        containerColor = color,
        shape = CircleShape,
        modifier = Modifier.size(80.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(36.dp))
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
