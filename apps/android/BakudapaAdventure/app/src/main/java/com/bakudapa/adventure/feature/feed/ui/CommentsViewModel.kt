package com.bakudapa.adventure.feature.feed.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.feed.domain.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: FeedRepository
) : BaseViewModel<CommentsState, CommentsEvent, CommentsEffect>(CommentsState()) {

    override fun onEvent(event: CommentsEvent) {
        when (event) {
            is CommentsEvent.LoadComments -> loadComments(event.postId)
            is CommentsEvent.OnInputChanged -> setState { it.copy(commentInput = event.text) }
            is CommentsEvent.SendComment -> sendComment(event.postId)
        }
    }

    private fun loadComments(postId: String) {
        viewModelScope.launch {
            repository.getComments(postId).collect { result ->
                when (result) {
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                    is DataResult.Success -> setState {
                        it.copy(isLoading = false, comments = result.data, error = null)
                    }
                    is DataResult.Error -> setState {
                        it.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
        }
    }

    private fun sendComment(postId: String) {
        val content = uiState.value.commentInput.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            setState { it.copy(isSending = true) }
            when (val result = repository.addComment(postId, content)) {
                is DataResult.Success -> {
                    setState { it.copy(isSending = false, commentInput = "") }
                    sendEffect(CommentsEffect.CommentSent)
                }
                is DataResult.Error -> {
                    setState { it.copy(isSending = false) }
                    sendEffect(CommentsEffect.ShowError(result.exception.message ?: "Gagal mengirim komentar"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
