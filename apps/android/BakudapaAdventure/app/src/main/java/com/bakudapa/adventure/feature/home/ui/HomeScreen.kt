package com.bakudapa.adventure.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.home.ui.components.*

@OptIn(Material3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToMountainDetail: (String) -> Unit,
    onNavigateToTrailDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToMountainDetail -> onNavigateToMountainDetail(effect.id)
                is HomeEffect.NavigateToTrailDetail -> onNavigateToTrailDetail(effect.id)
                is HomeEffect.NavigateToPostDetail -> { /* TODO */ }
                is HomeEffect.ShowError -> { /* TODO: Snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bakudapa Adventure",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Search Bar
            item {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(HomeEvent.OnSearchQueryChanged(it)) },
                    onSearch = { viewModel.onEvent(HomeEvent.OnSearchClicked) },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search mountains, trails...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { }
            }

            // 2. Quick Actions
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionButton(icon = Icons.Default.Terrain, label = "Mountains", onClick = { })
                    QuickActionButton(icon = Icons.Default.Map, label = "Maps", onClick = onNavigateToMap)
                    QuickActionButton(icon = Icons.Default.Explore, label = "Trails", onClick = { })
                    QuickActionButton(icon = Icons.Default.Group, label = "Community", onClick = { })
                }
            }

            // 3. Recommended Mountains
            item {
                SectionHeader(title = "Recommended")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(state.homeData?.recommendedMountains ?: emptyList()) { mountain ->
                        MountainCard(
                            mountain = mountain,
                            onClick = { viewModel.onEvent(HomeEvent.OnMountainClicked(mountain.id)) }
                        )
                    }
                }
            }

            // 4. Nearby Mountains
            item {
                SectionHeader(title = "Nearby You")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(state.homeData?.nearbyMountains ?: emptyList()) { mountain ->
                        MountainCard(
                            mountain = mountain,
                            onClick = { viewModel.onEvent(HomeEvent.OnMountainClicked(mountain.id)) }
                        )
                    }
                }
            }

            // 5. Popular Trails
            item {
                SectionHeader(title = "Popular Trails")
            }
            
            items(state.homeData?.popularTrails ?: emptyList()) { trail ->
                // Simple Trail item implementation
                ListItem(
                    headlineContent = { Text(trail.name) },
                    supportingContent = { Text("${trail.mountainName} • ${trail.distanceKm}km") },
                    trailingContent = { Text(trail.difficulty.name) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
