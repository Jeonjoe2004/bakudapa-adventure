package com.bakudapa.adventure.feature.profile.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var displayName by remember(state.profile) { mutableStateOf(state.profile?.name ?: "") }
    var photoUrl by remember(state.profile) { mutableStateOf(state.profile?.photoUrl ?: "") }
    val snackbarHostState = remember { SnackbarHostState() }
    var saveLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProfileEffect.NavigateToEditProfile -> {}
                is ProfileEffect.NavigateToAuth -> {}
                is ProfileEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is ProfileEffect.NavigateToFollowers -> {}
                is ProfileEffect.NavigateToFollowing -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            saveLoading = true
                            viewModel.onEvent(ProfileEvent.OnSaveProfile(displayName, photoUrl.ifBlank { null }))
                            saveLoading = false
                            onSaveComplete()
                        },
                        enabled = displayName.isNotBlank() && !saveLoading
                    ) {
                        if (saveLoading) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                if (photoUrl.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        content = {
                            Icon(
                                Icons.Default.Person,
                                null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        "Change Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                placeholder = { Text("Your name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("Photo URL") },
                placeholder = { Text("https://...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Paste image URL or leave blank") }
            )

            if (state.profile?.email != null) {
                OutlinedTextField(
                    value = state.profile!!.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    enabled = false,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
