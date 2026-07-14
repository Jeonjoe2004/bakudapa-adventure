package com.bakudapa.adventure.feature.trail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.bakudapa.adventure.core.ui.components.ShimmerItem
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    viewModel: TrailDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TrailDetailEffect.NavigateToTracking -> onNavigateToTracking(effect.trailId)
                is TrailDetailEffect.ShowError -> { /* Snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.trail?.name ?: "Trail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.trail == null) {
            ShimmerTrail(modifier = Modifier.padding(padding))
        } else if (state.error != null && state.trail == null) {
            TrailError(message = state.error!!, onRetry = { viewModel.onEvent(TrailDetailEvent.LoadTrail) })
        } else {
            state.trail?.let { trail ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    // Hero
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(trail.imageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)))
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            ) {
                                Text(trail.name, style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold, color = Color.White)
                                Text(trail.mountainName, style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                    }

                    // Stats row
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TrailStat("${trail.distanceKm}km", "Distance")
                            TrailStat(convertMinutes(trail.durationMinutes), "Duration")
                            TrailStat("${trail.elevationGain}m", "Elevation")
                        }
                    }

                    // Difficulty badge
                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DifficultyBadge(trail.difficulty)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Max elevation: ${trail.maxElevation}m",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Description
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        TrailSection(title = "Description") {
                            Text(
                                text = trail.description.ifEmpty { "No description available." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Recommended gear
                    if (trail.recommendedGear.isNotEmpty()) {
                        item { TrailSection(title = "Recommended Gear") {} }
                        items(trail.recommendedGear.size) { i ->
                            ListItem(
                                headlineContent = { Text(trail.recommendedGear[i]) },
                                leadingContent = {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        }
                    }

                    // Water sources
                    if (trail.waterSources.isNotEmpty()) {
                        item { TrailSection(title = "Water Sources") {} }
                        items(trail.waterSources.size) { i ->
                            ListItem(
                                headlineContent = { Text(trail.waterSources[i]) },
                                leadingContent = {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        }
                    }

                    // Camping spots
                    if (trail.campingSpots.isNotEmpty()) {
                        item { TrailSection(title = "Camping Spots") {} }
                        items(trail.campingSpots.size) { i ->
                            ListItem(
                                headlineContent = { Text(trail.campingSpots[i]) },
                                leadingContent = {
                                    Icon(Icons.Default.Home, contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        }
                    }

                    // Start button
                    item {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Button(
                                onClick = { viewModel.onEvent(TrailDetailEvent.OnStartTracking) },
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Start Tracking", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TrailStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val color = when (difficulty) {
        "EASY" -> Color(0xFF4CAF50)
        "MODERATE" -> Color(0xFFFFA000)
        "HARD" -> Color(0xFFF44336)
        "EXPERT" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text = difficulty,
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TrailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun ShimmerTrail(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ShimmerItem(Modifier.fillMaxWidth().height(220.dp))
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(3) { ShimmerItem(Modifier.size(40.dp)) }
        }
        repeat(4) { ShimmerItem(Modifier.fillMaxWidth().height(16.dp).padding(16.dp)) }
        repeat(3) { ShimmerItem(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 16.dp, vertical = 4.dp)) }
        ShimmerItem(Modifier.fillMaxWidth().height(50.dp).padding(16.dp))
    }
}

@Composable
private fun TrailError(message: String, onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Icon(Icons.Default.Refresh, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Retry") }
    }
}

private fun convertMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
