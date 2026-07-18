package com.bakudapa.adventure.feature.feed.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bakudapa.adventure.feature.feed.ui.components.PostItem
import com.bakudapa.adventure.core.ui.components.EmptyState
import com.bakudapa.adventure.feature.story.ui.StoryBar
import androidx.compose.material.icons.filled.RssFeed
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToComments: (String) -> Unit,
    onNavigateToCreatePost: () -> Unit,
    onNavigateToStoryViewer: (String) -> Unit = {},
    onNavigateToChat: (String) -> Unit = {},
    viewModel: FeedViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FeedEffect.NavigateToComments -> onNavigateToComments(effect.postId)
                is FeedEffect.NavigateToChat -> onNavigateToChat(effect.postId)
                is FeedEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                FeedEffect.PostCreated -> {
                    snackbarHostState.showSnackbar("Post shared!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePost) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.posts.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.posts.isEmpty()) {
                Text(text = "Error: ${state.error}", modifier = Modifier.align(Alignment.Center))
            } else if (state.posts.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.RssFeed,
                    title = "No Posts Yet",
                    description = "Be the first one to share your adventure with the community!",
                    actionLabel = "Create Post",
                    onActionClick = onNavigateToCreatePost
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Story bar di atas feed (Instagram-style)
                    item {
                        StoryBar(onUserStoryClick = onNavigateToStoryViewer)
                    }
                    items(state.posts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onLikeClick = { viewModel.onEvent(FeedEvent.OnLikeClicked(post.id, post.isLiked)) },
                            onCommentClick = { viewModel.onEvent(FeedEvent.OnCommentClicked(post.id)) },
                            onSaveClick = { viewModel.onEvent(FeedEvent.OnSaveClicked(post.id, post.isSaved)) },
                            onShareClick = { viewModel.onEvent(FeedEvent.OnShareClicked(post)) },
                            onRepostClick = { viewModel.onEvent(FeedEvent.OnRepostClicked(post)) },
                            onShareToChatClick = { viewModel.onEvent(FeedEvent.OnShareToChatClicked(post)) },
                            onReportClick = { viewModel.onEvent(FeedEvent.OnReportClicked(post.id)) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // FAB padding
                    }
                }
            }
        }
    }
}

