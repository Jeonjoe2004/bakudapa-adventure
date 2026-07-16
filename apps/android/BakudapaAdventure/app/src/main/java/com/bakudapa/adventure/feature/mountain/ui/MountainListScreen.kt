package com.bakudapa.adventure.feature.mountain.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.R
import com.bakudapa.adventure.core.ui.components.EmptyState
import com.bakudapa.adventure.core.ui.components.LoadingState
import com.bakudapa.adventure.feature.mountain.domain.model.MountainDifficulty
import com.bakudapa.adventure.feature.mountain.ui.components.MountainCard
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MountainListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: MountainListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MountainListEffect.NavigateToDetail -> onNavigateToDetail(effect.mountainId)
                is MountainListEffect.NavigateToMap -> onNavigateToMap()
                is MountainListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mountains_title)) },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(MountainListEvent.OnMapClicked) }) {
                        Icon(Icons.Default.Map, contentDescription = "Map")
                    }
                    IconButton(onClick = { viewModel.onEvent(MountainListEvent.OnFilterClicked) }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading && state.mountains.isEmpty()) {
            LoadingState(modifier = Modifier.padding(padding).fillMaxSize())
        } else if (state.error != null && state.mountains.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Error,
                title = stringResource(R.string.error),
                description = state.error!!,
                actionLabel = stringResource(R.string.retry),
                onActionClick = { viewModel.onEvent(MountainListEvent.LoadMountains) },
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(modifier = Modifier.padding(padding)) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onEvent(MountainListEvent.OnSearchQueryChanged(it)) },
                    onSearch = { viewModel.onEvent(MountainListEvent.OnSearchClicked) },
                    active = state.isSearchActive,
                    onActiveChange = { viewModel.onEvent(MountainListEvent.OnSearchActiveChanged(it)) },
                    placeholder = { Text(stringResource(R.string.search_mountains_hint)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) { }

                if (state.selectedDifficulty != null || state.minElevation > 0 || state.maxElevation > 0) {
                    FilterChipsRow(
                        selectedDifficulty = state.selectedDifficulty,
                        minElevation = state.minElevation,
                        maxElevation = state.maxElevation,
                        onClear = { viewModel.onEvent(MountainListEvent.OnClearFiltersClicked) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredMountains) { mountain ->
                        MountainCard(
                            mountain = mountain,
                            onClick = { viewModel.onEvent(MountainListEvent.OnMountainClicked(mountain.id)) }
                        )
                    }

                    if (state.filteredMountains.isEmpty() && state.searchQuery.isNotBlank()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.SearchOff,
                                title = stringResource(R.string.no_mountains_found),
                                description = "Try adjusting your search or filters",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedDifficulty: MountainDifficulty?,
    minElevation: Int,
    maxElevation: Int,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        selectedDifficulty?.let { difficulty ->
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("${difficulty.name} Difficulty") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        if (minElevation > 0) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("Min ${minElevation}m") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        if (maxElevation > 0) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("Max ${maxElevation}m") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        if (selectedDifficulty != null || minElevation > 0 || maxElevation > 0) {
            FilterChip(
                selected = false,
                onClick = onClear,
                label = { Text("Clear All") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun MountainListPreview() {
    MountainListScreen(
        onNavigateToDetail = {},
        onNavigateToMap = {}
    )
}
