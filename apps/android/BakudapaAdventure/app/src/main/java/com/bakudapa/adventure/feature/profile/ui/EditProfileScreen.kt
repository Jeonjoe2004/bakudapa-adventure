package com.bakudapa.adventure.feature.profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var displayName by remember(state.profile) { mutableStateOf(state.profile?.name ?: "") }
    var username by remember(state.profile) { mutableStateOf(state.profile?.username ?: "") }
    var bio by remember(state.profile) { mutableStateOf(state.profile?.bio ?: "") }
    var website by remember(state.profile) { mutableStateOf(state.profile?.website ?: "") }
    var photoUrl by remember(state.profile) { mutableStateOf(state.profile?.photoUrl ?: "") }
    var saveLoading by remember { mutableStateOf(false) }
    var uploadLoading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Image picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                uploadLoading = true
                try {
                    val filename = "profile_${FirebaseAuth.getInstance().currentUser?.uid}_${UUID.randomUUID()}.jpg"
                    val ref = FirebaseStorage.getInstance().getReference("profiles/$filename")
                    ref.putFile(it).await()
                    val downloadUrl = ref.downloadUrl.await().toString()
                    photoUrl = downloadUrl
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Gagal upload foto: ${e.message}")
                } finally {
                    uploadLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ProfileEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            saveLoading = true
                            viewModel.onEvent(
                                ProfileEvent.OnSaveProfile(
                                    name = displayName,
                                    username = username,
                                    bio = bio,
                                    website = website,
                                    photoUrl = photoUrl.ifBlank { null }
                                )
                            )
                            saveLoading = false
                            onSaveComplete()
                        },
                        enabled = displayName.isNotBlank() && !saveLoading && !uploadLoading
                    ) {
                        if (saveLoading) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Simpan", fontWeight = FontWeight.Bold)
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Avatar — tap to change (like Instagram)
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { launcher.launch("image/*") }
            ) {
                if (uploadLoading) {
                    Surface(
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                } else if (photoUrl.isNotBlank()) {
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
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, null,
                            modifier = Modifier.size(48.dp).align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Camera badge overlay
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt, "Ganti Foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(8.dp).align(Alignment.Center)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Username (@handle) — like Instagram
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                placeholder = { Text("@username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Tanda @ akan ditambahkan otomatis") }
            )

            // Display Name
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nama") },
                placeholder = { Text("Nama lengkap") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Bio
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                placeholder = { Text("Tentang kamu") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${bio.length}/150") }
            )

            // Website
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Tautan") },
                placeholder = { Text("https://") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
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
