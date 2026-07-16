package com.bakudapa.adventure.feature.leaderboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardEntry
import com.bakudapa.adventure.feature.leaderboard.domain.model.LeaderboardFilter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeaderboardScreen(
    onNavigateToUser: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val sortedEntries = viewModel.getSortedEntries()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LeaderboardEffect.NavigateToUser -> onNavigateToUser(effect.userId)
                is LeaderboardEffect.ShowError -> { /* snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Papan Peringkat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filter chips
            FlowRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LeaderboardFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = { viewModel.onEvent(LeaderboardEvent.OnFilterSelected(filter)) },
                        label = {
                            Text(when (filter) {
                                LeaderboardFilter.CLIMBS -> "Gunung"
                                LeaderboardFilter.DISTANCE -> "Jarak"
                                LeaderboardFilter.ELEVATION -> "Elevasi"
                                LeaderboardFilter.BADGES -> "Badge"
                            })
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (filter) {
                                    LeaderboardFilter.CLIMBS -> Icons.Default.Hiking
                                    LeaderboardFilter.DISTANCE -> Icons.Default.Straighten
                                    LeaderboardFilter.ELEVATION -> Icons.Default.Terrain
                                    LeaderboardFilter.BADGES -> Icons.Default.EmojiEvents
                                },
                                contentDescription = null, modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            when {
                state.isLoading && state.entries.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Gagal memuat: ${state.error}", color = MaterialTheme.colorScheme.error)
                    }
                }
                sortedEntries.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null,
                                modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.height(8.dp))
                            Text("Belum ada data pendaki", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(sortedEntries, key = { _, e -> e.userId }) { _, entry ->
                            LeaderboardItem(
                                entry = entry,
                                onClick = { viewModel.onEvent(LeaderboardEvent.OnUserClicked(entry.userId)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    entry: LeaderboardEntry,
    onClick: () -> Unit
) {
    val rankColor = when (entry.rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    ListItem(
        headlineContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#${entry.rank} ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(entry.displayName, fontWeight = FontWeight.SemiBold)
            }
        },
        supportingContent = {
            Text("${entry.mountainsClimbed} gunung  •  ${"%.1f".format(entry.totalDistanceKm)} km  •  ${entry.totalElevationM}m elevasi")
        },
        leadingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entry.rank <= 3) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(rankColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${entry.rank}", fontWeight = FontWeight.Bold,
                            color = if (entry.rank == 1) Color.Black else Color.White
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(entry.photoUrl),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        trailingContent = {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (entry.rank == 1) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
}
