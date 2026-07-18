package com.bakudapa.adventure.feature.summit.ui

import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSummitLogScreen(
    mountainId: String,
    mountainName: String,
    onNavigateBack: () -> Unit,
    viewModel: CreateSummitLogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var radiusCheck by remember { mutableStateOf<Boolean?>(null) }
    var radiusChecking by remember { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.onEvent(CreateSummitLogEvent.OnPhotoSelected(uri))
    }

    LaunchedEffect(mountainId) {
        radiusChecking = true
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedClient.lastLocation.await()
            if (location != null) {
                val doc = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("mountains").document(mountainId).get().await()
                val mLat = doc.getDouble("latitude")
                val mLng = doc.getDouble("longitude")
                if (mLat != null && mLng != null) {
                    val dist = haversine(location.latitude, location.longitude, mLat, mLng)
                    radiusCheck = dist <= 1000.0
                } else radiusCheck = true
            } else radiusCheck = true
        } catch (_: Exception) { radiusCheck = true }
        radiusChecking = false
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                CreateSummitLogEffect.SummitLogCreated -> onNavigateBack()
                is CreateSummitLogEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Saya Sampai Puncak!") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.onEvent(CreateSummitLogEvent.OnSubmit(mountainId, mountainName)) },
                        enabled = !state.isSaving && radiusCheck != false
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Post", fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // GPS Radius check
            if (radiusChecking) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Memverifikasi lokasi...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (radiusCheck == false) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Lokasi terlalu jauh", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text("Kamu harus di radius 1km dari puncak ${mountainName}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(state.photoUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        onClick = { launcher.launch("image/*") },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Foto puncak", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            if (state.photoUri == null) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ambil / Pilih Foto")
                }
            } else {
                TextButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ganti Foto")
                }
            }

            // Caption
            OutlinedTextField(
                value = state.caption,
                onValueChange = { viewModel.onEvent(CreateSummitLogEvent.OnCaptionChanged(it)) },
                label = { Text("Catatan") },
                placeholder = { Text("Akhirnya sampai puncak ${mountainName}!...") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Ceritakan momen puncakmu") }
            )

            // Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Flag, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(mountainName, fontWeight = FontWeight.Bold)
                        Text("Summit check-in", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// Utility untuk hitung jarak GPS
private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2).pow(2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2).pow(2)
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}
