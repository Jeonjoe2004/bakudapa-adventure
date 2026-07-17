package com.bakudapa.adventure.feature.story.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun StoryViewerScreen(
    userId: String,
    onClose: () -> Unit,
    viewModel: StoryViewerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        viewModel.onEvent(StoryViewerEvent.LoadStories(userId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                StoryViewerEffect.CloseViewer -> onClose()
                is StoryViewerEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    // Auto-progress timer
    val progress by animateFloatAsState(
        targetValue = if (state.isPaused) 0f else 1f,
        animationSpec = tween(durationMillis = if (state.isPaused) 999999 else 5000),
        label = "storyProgress"
    )

    LaunchedEffect(state.currentIndex, state.isPaused) {
        if (!state.isPaused) {
            delay(5000)
            viewModel.onEvent(StoryViewerEvent.OnNextStory)
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    if (state.stories.isEmpty()) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Text("No stories", color = Color.White)
        }
        return
    }

    val currentStory = state.stories.getOrNull(state.currentIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Story image (full screen)
        currentStory?.let { story ->
            Image(
                painter = rememberAsyncImagePainter(story.mediaUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // Top bar: progress indicators + close
        Column(modifier = Modifier.fillMaxSize()) {
            // Progress bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 8.dp, 8.dp, 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                state.stories.forEachIndexed { index, _ ->
                    val barProgress by animateFloatAsState(
                        targetValue = if (index < state.currentIndex) 1f
                                    else if (index == state.currentIndex) progress
                                    else 0f,
                        animationSpec = tween(300),
                        label = "bar$index"
                    )
                    LinearProgressIndicator(
                        progress = { barProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(MaterialTheme.shapes.extraSmall),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            // Header: avatar, username, close
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(state.userPhotoUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = state.username,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { viewModel.onEvent(StoryViewerEvent.OnClose) }) {
                    Icon(Icons.Default.Close, "Close", tint = Color.White)
                }
            }

            // Swipeable content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // handled by tap
                            },
                            onHorizontalDrag = { _, _ -> }
                        )
                    }
                    .clickable {
                        viewModel.onEvent(StoryViewerEvent.OnTogglePause)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (currentStory?.caption?.isNotBlank() == true) {
                    Text(
                        text = currentStory.caption,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(24.dp)
                    )
                }
            }

            // Bottom tap zones
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                Row(Modifier.fillMaxSize()) {
                    // Tap left → previous
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable { viewModel.onEvent(StoryViewerEvent.OnPreviousStory) }
                    )
                    // Tap right → next
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable { viewModel.onEvent(StoryViewerEvent.OnNextStory) }
                    )
                }
            }
        }
    }
}
