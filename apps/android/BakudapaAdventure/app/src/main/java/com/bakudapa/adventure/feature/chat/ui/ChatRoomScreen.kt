package com.bakudapa.adventure.feature.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(roomId) {
        viewModel.onEvent(ChatRoomEvent.LoadMessages(roomId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            if (effect is ChatRoomEffect.ScrollToBottom) {
                if (state.messages.isNotEmpty()) {
                    listState.animateScrollToItem(state.messages.size - 1)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = state.room?.name ?: "Chat")
                        if (state.isOtherUserTyping) {
                            Text(text = "typing...", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = state.currentMessage,
                        onValueChange = { 
                            viewModel.onEvent(ChatRoomEvent.OnMessageChanged(it))
                            viewModel.onEvent(ChatRoomEvent.OnTyping(it.isNotEmpty()))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Message") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                    IconButton(onClick = { viewModel.onEvent(ChatRoomEvent.OnSendClicked) }) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(state.messages) { message ->
                MessageItem(
                    message = message,
                    isMine = message.senderId == currentUserId
                )
            }
        }
    }
}
