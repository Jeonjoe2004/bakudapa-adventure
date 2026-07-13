package com.bakudapa.adventure.feature.feed.ui

import android.net.Uri
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.feed.domain.model.Post

data class FeedState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
    val isCreatingPost: Boolean = false,
    val newPostContent: String = "",
    val newPostMediaUri: Uri? = null
) : UiState

sealed class FeedEvent : UiEvent {
    object LoadPosts : FeedEvent()
    data class OnLikeClicked(val postId: String, val isLiked: Boolean) : FeedEvent()
    data class OnSaveClicked(val postId: String, val isSaved: Boolean) : FeedEvent()
    data class OnCommentClicked(val postId: String) : FeedEvent()
    data class OnShareClicked(val post: Post) : FeedEvent()
    data class OnReportClicked(val postId: String) : FeedEvent()
    
    // Create Post Events
    data class OnNewPostContentChanged(val content: String) : FeedEvent()
    data class OnNewPostMediaSelected(val uri: Uri?) : FeedEvent()
    object OnCreatePostClicked : FeedEvent()
}

sealed class FeedEffect : UiEffect {
    data class NavigateToComments(val postId: String) : FeedEffect()
    data class ShowError(val message: String) : FeedEffect()
    object PostCreated : FeedEffect()
}
