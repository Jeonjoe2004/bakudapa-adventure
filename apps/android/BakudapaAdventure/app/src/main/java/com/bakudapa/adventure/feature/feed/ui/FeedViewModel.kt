package com.bakudapa.adventure.feature.feed.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.feed.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: FeedRepository
) : BaseViewModel<FeedState, FeedEvent, FeedEffect>(FeedState()) {

    init {
        loadPosts()
    }

    override fun onEvent(event: FeedEvent) {
        when (event) {
            FeedEvent.LoadPosts -> loadPosts()
            is FeedEvent.OnLikeClicked -> handleLike(event.postId, event.isLiked)
            is FeedEvent.OnSaveClicked -> handleSave(event.postId, event.isSaved)
            is FeedEvent.OnCommentClicked -> sendEffect(FeedEffect.NavigateToComments(event.postId))
            is FeedEvent.OnShareClicked -> { /* TODO: Implement share intent */ }
            is FeedEvent.OnReportClicked -> handleReport(event.postId)
            is FeedEvent.OnNewPostContentChanged -> setState { it.copy(newPostContent = event.content) }
            is FeedEvent.OnNewPostMediaSelected -> setState { it.copy(newPostMediaUri = event.uri) }
            FeedEvent.OnCreatePostClicked -> createPost()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            repository.getPosts().collect { result ->
                when (result) {
                    is DataResult.Success -> setState { it.copy(posts = result.data, isLoading = false) }
                    is DataResult.Error -> setState { it.copy(error = result.exception.message, isLoading = false) }
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun handleLike(postId: String, isLiked: Boolean) {
        viewModelScope.launch {
            if (isLiked) repository.unlikePost(postId) else repository.likePost(postId)
        }
    }

    private fun handleSave(postId: String, isSaved: Boolean) {
        viewModelScope.launch {
            if (isSaved) repository.unsavePost(postId) else repository.savePost(postId)
        }
    }

    private fun handleReport(postId: String) {
        viewModelScope.launch {
            repository.reportPost(postId, "Inappropriate content")
            sendEffect(FeedEffect.ShowError("Post reported. Thank you."))
        }
    }

    private fun createPost() {
        val content = uiState.value.newPostContent
        val mediaUri = uiState.value.newPostMediaUri
        
        if (content.isBlank() && mediaUri == null) return
        
        viewModelScope.launch {
            setState { it.copy(isCreatingPost = true) }
            val result = repository.createPost(content, mediaUri, emptyList(), emptyList())
            when (result) {
                is DataResult.Success -> {
                    setState { it.copy(isCreatingPost = false, newPostContent = "", newPostMediaUri = null) }
                    sendEffect(FeedEffect.PostCreated)
                }
                is DataResult.Error -> {
                    setState { it.copy(isCreatingPost = false) }
                    sendEffect(FeedEffect.ShowError(result.exception.message ?: "Failed to create post"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
