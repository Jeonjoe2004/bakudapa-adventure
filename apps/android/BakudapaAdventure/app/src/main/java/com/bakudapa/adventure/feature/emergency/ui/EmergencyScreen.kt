package com.bakudapa.adventure.feature.emergency.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showSOSConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is EmergencyEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, relation ->
                viewModel.onEvent(EmergencyEvent.AddContact(name, phone, relation))
                showAddDialog = false
            }
        )
    }

    // SOS Confirmation Dialog
    if (showSOSConfirm) {
        AlertDialog(
            onDismissRequest = { showSOSConfirm = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Peringatan",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Red
                )
            },
            title = { Text("Kirim SOS?") },
            text = {
                Text(
                    "Sinyal darurat akan dikirim ke kontak darurat dan pengguna terdekat. " +
                            "Pastikan kamu benar-benar dalam keadaan darurat."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSOSConfirm = false
                        viewModel.onEvent(EmergencyEvent.TriggerSOS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Kirim SOS", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSOSConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Emergency Center") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SOS Button
            Button(
                onClick = { showSOSConfirm = true },
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(48.dp))
                    Text("SOS", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }

            if (state.isSOSActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color.Red.copy(alpha = 0.12f)
                ) {
                    Text(
                        "SOS AKTIF - Bantuan sedang dalam perjalanan",
                        color = Color.Red,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Emergency Contacts (${state.contacts.size})",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            if (state.contacts.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Belum ada kontak darurat. Tambahkan kontak terpercaya yang bisa dihubungi saat darurat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.contacts, key = { it.id }) { contact ->
                    ListItem(
                        headlineContent = { Text(contact.name, fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("${contact.relation} • ${contact.phoneNumber}") },
                        trailingContent = {
                            IconButton(onClick = { viewModel.onEvent(EmergencyEvent.DeleteContact(contact.id)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    )
                }

                item {
                    TextButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah kontak")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Emergency Contact")
                    }
                }
            }

            if (state.activeAlerts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Nearby SOS Alerts!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(Modifier.height(8.dp))
                        state.activeAlerts.forEach { alert ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${alert.userName}: ${alert.message}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "${"%.4f".format(alert.latitude)}, ${"%.4f".format(alert.longitude)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                TextButton(onClick = { viewModel.onEvent(EmergencyEvent.ResolveSOS(alert.id)) }) {
                                    Text("Resolve", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            if (alert != state.activeAlerts.last()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, relation: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }
    val isValid = name.isNotBlank() && phone.isNotBlank() && relation.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama") },
                    placeholder = { Text("Contoh: Gunung Guide") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. Telepon") },
                    placeholder = { Text("08xxxxxxxxxx") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = { Text("Hubungan") },
                    placeholder = { Text("Keluarga, Teman, Guide, etc") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name.trim(), phone.trim(), relation.trim()) }, enabled = isValid) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
