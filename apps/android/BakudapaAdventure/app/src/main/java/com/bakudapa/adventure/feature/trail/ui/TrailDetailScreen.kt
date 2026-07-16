package com.bakudapa.adventure.feature.trail.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo
import com.bakudapa.adventure.feature.trail.domain.model.Checkpoint
import com.bakudapa.adventure.feature.trail.domain.model.PointOfInterest
import com.bakudapa.adventure.feature.trail.domain.model.PoiType
import com.bakudapa.adventure.feature.trail.domain.model.TrailDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToTrail: (String) -> Unit = {},
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
                            DifficultyBadge(trail.difficulty.name)
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

                    // Points of Interest
                    val poiByType = trail.pointsOfInterest.groupBy { it.type }

                    if (poiByType.isNotEmpty()) {
                        PoiSection(
                            title = "Camping Grounds",
                            pois = poiByType[PoiType.CAMPING_GROUND] ?: emptyList(),
                            icon = Icons.Default.Home,
                        )
                        PoiSection(
                            title = "Water Sources",
                            pois = poiByType[PoiType.WATER_SOURCE] ?: emptyList(),
                            icon = Icons.Default.WaterDrop,
                        )
                        PoiSection(
                            title = "Shelters",
                            pois = poiByType[PoiType.SHELTER] ?: emptyList(),
                            icon = Icons.Default.LocationOn,
                        )
                    }

                    // Danger Zones
                    val dangerZones = poiByType[PoiType.DANGER_ZONE] ?: emptyList()
                    if (dangerZones.isNotEmpty()) {
                        item {
                            TrailSection(title = "⚠ Danger Zones") {}
                        }
                        items(dangerZones.size) { i ->
                            val dz = dangerZones[i]
                            ListItem(
                                headlineContent = { Text(dz.name) },
                                supportingContent = {
                                    if (dz.description.isNotEmpty()) Text(dz.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                                },
                                leadingContent = {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                }
                            )
                        }
                    }

                    // Elevation Profile
                    item {
                        ElevationProfileSection(
                            checkpoints = trail.checkpoints,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Reviews - LazyListScope extension, call directly
                    TrailReviewSection(
                        reviews = state.reviews,
                        reviewInput = state.reviewInput,
                        reviewRating = state.reviewRating,
                        isSending = state.isSending,
                        onInputChanged = { viewModel.onEvent(TrailDetailEvent.ReviewInputChanged(it)) },
                        onRatingChanged = { viewModel.onEvent(TrailDetailEvent.ReviewRatingChanged(it)) },
                        onSend = { viewModel.onEvent(TrailDetailEvent.ReviewSend) }
                    )

                    // Alternative Trails
                    item {
                        AlternativeTrailsSection(
                            alternativeTrails = state.alternativeTrails.filter { it.id != trail.id },
                            onTrailClick = { trailId ->
                                onNavigateToTrail(trailId)
                            }
                        )
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

private fun LazyListScope.PoiSection(
    title: String,
    pois: List<PointOfInterest>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    if (pois.isEmpty()) return
    item {
        TrailSection(title = title) {}
    }
    items(pois.size) { i ->
        val poi = pois[i]
        ListItem(
            headlineContent = { Text(poi.name) },
            supportingContent = {
                if (poi.elevation > 0 || poi.description.isNotEmpty()) {
                    Text(
                        buildString {
                            if (poi.elevation > 0) append("${poi.elevation}m")
                            if (poi.elevation > 0 && poi.description.isNotEmpty()) append(" • ")
                            if (poi.description.isNotEmpty()) append(poi.description)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            leadingContent = {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        )
    }
}

private fun convertMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

@Composable
fun AlternativeTrailsSection(
    alternativeTrails: List<TrailInfo>,
    onTrailClick: (String) -> Unit
) {
    if (alternativeTrails.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Other Trails",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        alternativeTrails.forEach { trail ->
            Surface(
                onClick = { onTrailClick(trail.id) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(trail.name, fontWeight = FontWeight.SemiBold)
                        Text("${trail.distanceKm}km • ${trail.durationMinutes}min • ${trail.difficulty.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
