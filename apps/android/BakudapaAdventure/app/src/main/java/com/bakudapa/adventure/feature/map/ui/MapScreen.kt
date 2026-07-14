package com.bakudapa.adventure.feature.map.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

    HandlePermissions(
        permissions = PermissionUtils.locationPermissions.toList(),
        rationaleMessage = stringResource(R.string.permission_location_rationale),
        onPermissionGranted = {
            MapContent(state, onNavigateBack, viewModel)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapContent(
    state: MapState,
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mountain Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(MapEvent.OnDownloadMapClicked) }) {
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
        Column(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            MountainMap(
                markers = state.markers,
                userLocation = state.userLocation,
                onMarkerClick = { viewModel.onEvent(MapEvent.OnMarkerClicked(it)) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

