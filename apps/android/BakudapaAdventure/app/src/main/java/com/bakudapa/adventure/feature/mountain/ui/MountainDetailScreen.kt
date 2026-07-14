package com.bakudapa.adventure.feature.mountain.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.bakudapa.adventure.feature.mountain.domain.model.MountainSection
import com.bakudapa.adventure.feature.mountain.domain.model.SectionType
import com.bakudapa.adventure.feature.mountain.domain.model.TrailInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MountainDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTrail: (String) -> Unit,
    onNavigateToMap: (Double, Double, String) -> Unit,
    viewModel: MountainDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MountainDetailEffect.NavigateToTrail -> onNavigateToTrail(effect.id)
                is MountainDetailEffect.NavigateToMap -> onNavigateToMap(effect.lat, effect.lng, effect.name)
                is MountainDetailEffect.ShowError -> { /* Snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.mountain?.name ?: "Mountain") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.mountain == null) {
            ShimmerDetail(modifier = Modifier.padding(padding))
        } else if (state.error != null && state.mountain == null) {
            ErrorState(message = state.error!!, onRetry = { viewModel.onEvent(MountainDetailEvent.LoadMountain) })
        } else {
            state.mountain?.let { mountain ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Hero image
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(mountain.imageUrl),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )
                            Column(
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            ) {
                                Text(
                                    text = mountain.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = mountain.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Quick stats row
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip(icon = Icons.Default.Height, value = "${mountain.elevation}m", label = "Elevation")
                            StatChip(icon = Icons.Default.Star, value = mountain.rating.toString(), label = "Rating")
                            StatChip(icon = Icons.Default.Terrain, value = mountain.difficulty, label = "Difficulty")
                        }
                    }

                    // Description
                    item {
                        SectionDetail(title = "About") {
                            Text(
                                text = mountain.description.ifEmpty { "No description available." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Best season
                    if (mountain.bestSeason.isNotBlank()) {
                        item {
                            SectionDetail(title = "Best Season") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = mountain.bestSeason,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Open Map button
                    if (mountain.latitude != 0.0) {
                        item {
                            Button(
                                onClick = { viewModel.onEvent(MountainDetailEvent.OnOpenMapClicked) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("View on Map")
                            }
                        }
                    }

                    // Trails section
                    item { Spacer(Modifier.height(8.dp)) }
                    item {
                        SectionDetail(title = "Trails") {
                            if (state.trails.isEmpty()) {
                                Text(
                                    text = "No trails available.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    items(state.trails) { trail ->
                        TrailCard(
                            trail = trail,
                            onClick = { viewModel.onEvent(MountainDetailEvent.OnTrailClicked(trail.id)) }
                        )
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun SectionDetail(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrailCard(
    trail: TrailInfo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(trail.imageUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trail.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${trail.distanceKm}km • ${trail.durationMinutes}min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(4.dp))
                TrailDifficultyChip(trail.difficulty)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun TrailDifficultyChip(difficulty: String) {
    val color = when (difficulty) {
        "EASY" -> Color(0xFF4CAF50)
        "MODERATE" -> Color(0xFFFFA000)
        "HARD" -> Color(0xFFF44336)
        "EXPERT" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = difficulty,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ShimmerDetail(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ShimmerItem(modifier = Modifier.fillMaxWidth().height(250.dp))
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            repeat(3) { ShimmerItem(modifier = Modifier.size(48.dp)) }
        }
        Spacer(Modifier.height(16.dp))
        repeat(3) { ShimmerItem(modifier = Modifier.fillMaxWidth().height(16.dp).padding(horizontal = 16.dp, vertical = 4.dp)) }
        Spacer(Modifier.height(16.dp))
        repeat(2) { ShimmerItem(modifier = Modifier.fillMaxWidth().height(80.dp).padding(horizontal = 16.dp, vertical = 4.dp)) }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Retry")
        }
    }
}
