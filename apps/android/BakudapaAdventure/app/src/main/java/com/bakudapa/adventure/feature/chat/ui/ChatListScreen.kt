package com.bakudapa.adventure.feature.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddComment
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
fun ChatListScreen(
    onNavigateToRoom: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            if (effect is ChatListEffect.NavigateToChatRoom) {
                onNavigateToRoom(effect.roomId)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Messages", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(ChatListEvent.OnCreateGroupClicked) }) {
                Icon(Icons.Default.AddComment, contentDescription = "New Message")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(state.rooms) { room ->
                    ListItem(
                        headlineContent = { Text(room.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(room.lastMessage?.content ?: "No messages", maxLines = 1) },
                        leadingContent = {
                            Image(
                                painter = rememberAsyncImagePainter(room.iconUrl),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        },
                        trailingContent = {
                            if (room.unreadCount > 0) {
                                Badge { Text(room.unreadCount.toString()) }
                            }
                        },
                        modifier = Modifier.clickable { viewModel.onEvent(ChatListEvent.OnRoomClicked(room.id)) }
                    )
                }
            }
        }
    }
}
