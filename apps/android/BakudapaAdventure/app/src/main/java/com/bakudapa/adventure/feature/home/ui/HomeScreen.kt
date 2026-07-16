package com.bakudapa.adventure.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.core.ui.components.ShimmerItem
import com.bakudapa.adventure.feature.home.ui.components.*
import com.bakudapa.adventure.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToMountainDetail: (String) -> Unit,
    onNavigateToTrailDetail: (String) -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToMountainList: () -> Unit,
    onNavigateToFeed: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToMountainDetail -> onNavigateToMountainDetail(effect.id)
                is HomeEffect.NavigateToTrailDetail -> onNavigateToTrailDetail(effect.id)
                is HomeEffect.NavigateToPostDetail -> onNavigateToPostDetail(effect.id)
                is HomeEffect.NavigateToMountainList -> onNavigateToMountainList()
                is HomeEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(onClick = onNavigateToNotifications) {
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
            if (state.isLoading) {
                // Shimmer Loading UI
                item {
                    ShimmerItem(modifier = Modifier.fillMaxWidth().height(120.dp).padding(16.dp))
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        repeat(4) { ShimmerItem(modifier = Modifier.size(56.dp).clip(CircleShape)) }
                    }
                    repeat(2) {
                        ShimmerItem(modifier = Modifier.width(150.dp).height(24.dp).padding(16.dp))
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(3) { ShimmerItem(modifier = Modifier.width(200.dp).height(250.dp).padding(8.dp)) }
                        }
                    }
                }
            } else {
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

                // 2. Weather Section
                item {
                    state.homeData?.weather?.let { weather ->
                        WeatherCard(weather = weather)
                    }
                }

                // 3. Quick Actions
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(icon = Icons.Default.Terrain, label = "Mountains", onClick = onNavigateToMountainList)
                        QuickActionButton(icon = Icons.Default.Map, label = "Maps", onClick = onNavigateToMap)
                        QuickActionButton(icon = Icons.Default.Explore, label = "Trails", onClick = onNavigateToMountainList)
                        QuickActionButton(icon = Icons.Default.Group, label = "Community", onClick = onNavigateToFeed)
                    }
                }

                // 4. Recommended Mountains
                item {
                    SectionHeaderWithAction(
                        title = "Recommended",
                        actionText = stringResource(R.string.view_all),
                        onActionClick = onNavigateToMountainList
                    )
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

                // 5. Nearby Mountains
                item {
                    SectionHeaderWithAction(
                        title = "Nearby You",
                        actionText = stringResource(R.string.view_all),
                        onActionClick = onNavigateToMountainList
                    )
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

                // 6. Popular Trails
                item {
                    SectionHeader(title = "Popular Trails")
                }
                
                items(state.homeData?.popularTrails ?: emptyList()) { trail ->
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
        }
    }
}

