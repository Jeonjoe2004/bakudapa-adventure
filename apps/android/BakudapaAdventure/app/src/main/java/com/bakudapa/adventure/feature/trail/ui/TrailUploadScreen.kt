package com.bakudapa.adventure.feature.trail.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.trail.domain.model.PointOfInterest
import com.bakudapa.adventure.feature.trail.domain.model.PoiType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailUploadScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrailUploadViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPoiDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            Toast.makeText(context, "Trail submitted for review", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Trail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.name, onValueChange = { viewModel.onEvent(TrailUploadEvent.NameChanged(it)) },
                    label = { Text("Trail Name") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.mountainName, onValueChange = { viewModel.onEvent(TrailUploadEvent.MountainNameChanged(it)) },
                        label = { Text("Mountain Name") }, modifier = Modifier.weight(1f),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.difficulty, onValueChange = { viewModel.onEvent(TrailUploadEvent.DifficultyChanged(it)) },
                        label = { Text("Difficulty") }, modifier = Modifier.width(120.dp),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = if (state.durationMinutes == 0) "" else state.durationMinutes.toString(),
                        onValueChange = { viewModel.onEvent(TrailUploadEvent.DurationChanged(it.toIntOrNull() ?: 0)) },
                        label = { Text("Duration (min)") }, modifier = Modifier.weight(1f),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = if (state.distanceKm == 0.0) "" else state.distanceKm.toString(),
                        onValueChange = { viewModel.onEvent(TrailUploadEvent.DistanceChanged(it.toDoubleOrNull() ?: 0.0)) },
                        label = { Text("Distance (km)") }, modifier = Modifier.weight(1f),
                        singleLine = true, shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = state.description, onValueChange = { viewModel.onEvent(TrailUploadEvent.DescriptionChanged(it)) },
                    label = { Text("Description") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 3, maxLines = 5, shape = RoundedCornerShape(12.dp)
                )
            }

            // POI section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Points of Interest", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showPoiDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add")
                    }
                }
            }

            items(state.pois) { poi ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(poi.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("${poi.type.name} • ${poi.elevation}m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        IconButton(onClick = { viewModel.onEvent(TrailUploadEvent.RemovePoi(poi)) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // Submit
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.onEvent(TrailUploadEvent.Submit) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state.name.isNotBlank() && !state.isSaving,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Submit Trail for Review", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // POI dialog
    if (showPoiDialog) {
        PoiDialog(
            onDismiss = { showPoiDialog = false },
            onAdd = { poi -> viewModel.onEvent(TrailUploadEvent.AddPoi(poi)); showPoiDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoiDialog(
    onDismiss: () -> Unit,
    onAdd: (PointOfInterest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PoiType.CAMPING_GROUND) }
    var elevation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Point of Interest") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(value = type.name, onValueChange = {}, readOnly = true, label = { Text("Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        PoiType.entries.forEach { t ->
                            DropdownMenuItem(text = { Text(t.name) }, onClick = { type = t; expanded = false })
                        }
                    }
                }
                OutlinedTextField(value = elevation, onValueChange = { elevation = it }, label = { Text("Elevation (m)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onAdd(PointOfInterest(name = name, type = type, elevation = elevation.toIntOrNull() ?: 0, description = description))
            }, enabled = name.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
