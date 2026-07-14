package com.bakudapa.adventure.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.settings.domain.model.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateToAuth -> onNavigateToAuth()
                is SettingsEffect.ShowToast -> { /* Snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            item {
                SettingsHeader("Account")
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    onClick = onNavigateToEditProfile
                )
            }

            item {
                SettingsHeader("Preferences")
                SettingsToggleItem(
                    icon = Icons.Default.Palette,
                    title = "Dark Mode",
                    checked = state.settings.isDarkMode,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleDarkMode(it)) }
                )
                SettingsToggleItem(
                    icon = Icons.Default.Straighten,
                    title = "Metric Units",
                    subtitle = if (state.settings.isMetricUnit) "km, m" else "mi, ft",
                    checked = state.settings.isMetricUnit,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleMetricUnit(it)) }
                )
                SettingsToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    checked = state.settings.notificationsEnabled,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleNotifications(it)) }
                )
            }

            item {
                SettingsHeader("About")
                SettingsItem(title = "App Version", subtitle = "1.0.0 (Build 1)")
                SettingsItem(title = "Terms of Service")
                SettingsItem(title = "Privacy Policy")
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.onEvent(SettingsEvent.OnLogoutClicked) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { subtitle?.let { Text(it) } },
        leadingContent = { icon?.let { Icon(it, contentDescription = null) } },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    )
}

@Composable
fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { subtitle?.let { Text(it) } },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    )
}
