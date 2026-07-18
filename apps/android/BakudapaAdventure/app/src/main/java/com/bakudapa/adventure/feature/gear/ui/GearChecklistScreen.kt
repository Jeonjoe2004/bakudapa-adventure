package com.bakudapa.adventure.feature.gear.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.gear.domain.model.GearCategory
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearChecklistScreen(
    mountainId: String,
    mountainName: String,
    onNavigateBack: () -> Unit,
    viewModel: GearViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mountainId) {
        viewModel.onEvent(GearEvent.LoadChecklist(mountainId, mountainName))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            if (effect is GearEffect.ShowToast) snackbarHostState.showSnackbar(effect.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gear Checklist") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onEvent(GearEvent.SaveChecklist) }) {
                        Icon(Icons.Default.Save, contentDescription = "Simpan", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Simpan")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header info
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(mountainName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${state.checkedCount}/${state.totalCount} barang siap",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (state.totalCount > 0) state.checkedCount.toFloat() / state.totalCount else 0f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            // Preset selector
            ScrollableTabRow(
                selectedTabIndex = state.selectedPresetIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                state.presets.forEachIndexed { index, preset ->
                    Tab(
                        selected = state.selectedPresetIndex == index,
                        onClick = { viewModel.onEvent(GearEvent.SelectPreset(index)) },
                        text = { Text(preset.name, maxLines = 1) }
                    )
                }
            }

            // Checklist items, grouped by category
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val grouped = state.items.groupBy { it.category }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    grouped.forEach { (category, items) ->
                        item {
                            Text(
                                category.label,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        }
                        items(items, key = { it.id }) { item ->
                            GearChecklistItem(
                                item = item,
                                onClick = { viewModel.onEvent(GearEvent.ToggleItem(item.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GearChecklistItem(
    item: com.bakudapa.adventure.feature.gear.domain.model.GearItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = if (item.isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surface,
        tonalElevation = if (item.isChecked) 0.dp else 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(item.icon, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            Spacer(Modifier.width(12.dp))
            Text(
                item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Medium,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (item.isChecked) "Sudah siap" else "Belum siap",
                tint = if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
