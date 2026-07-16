package com.bakudapa.adventure.feature.chat.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.chat.ui.components.MessageItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    roomId: String,
    onNavigateBack: () -> Unit,
    auth: FirebaseAuth,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val currentUserId = auth.currentUser?.uid
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.sendMedia(uri, isImage = true)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) viewModel.sendMedia(uri, isImage = false)
    }

    LaunchedEffect(roomId) {
        viewModel.onEvent(ChatRoomEvent.LoadMessages(roomId))
    }

    // Scroll ke bawah saat pesan baru masuk atau effect ScrollToBottom
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChatRoomEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        listState.animateScrollToItem(state.messages.size - 1)
                    }
                }
                is ChatRoomEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is ChatRoomEffect.PickMedia -> imagePickerLauncher.launch("image/*")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.room?.name ?: "Chat",
                            fontWeight = FontWeight.SemiBold
                        )
                        when {
                            state.isOtherUserTyping ->
                                Text(
                                    text = "sedang mengetik...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            state.otherUserOnline ->
                                Text(
                                    text = "online",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            MessageInputBar(
                value = state.currentMessage,
                isUploading = state.isUploadingMedia,
                onValueChange = { text ->
                    viewModel.onEvent(ChatRoomEvent.OnMessageChanged(text))
                    viewModel.onEvent(ChatRoomEvent.OnTyping(text.isNotEmpty()))
                },
                onSend = { viewModel.onEvent(ChatRoomEvent.OnSendClicked) },
                onAttachImage = { imagePickerLauncher.launch("image/*") },
                onAttachFile = { filePickerLauncher.launch("*/*") }
            )
        }
    ) { padding ->
        if (state.isLoading && state.messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    MessageItem(
                        message = message,
                        isMine = message.senderId == currentUserId
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    value: String,
    isUploading: Boolean = false,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit = {},
    onAttachFile: () -> Unit = {}
) {
    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attach image
            IconButton(onClick = onAttachImage, enabled = !isUploading) {
                Icon(Icons.Default.AttachFile, contentDescription = "Lampirkan gambar")
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis pesan...") },
                shape = RoundedCornerShape(24.dp),
                maxLines = 5,
                enabled = !isUploading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                val canSend = value.isNotBlank()
                FilledIconButton(
                    onClick = onSend,
                    enabled = canSend,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Kirim")
                }
            }
        }
    }
}
