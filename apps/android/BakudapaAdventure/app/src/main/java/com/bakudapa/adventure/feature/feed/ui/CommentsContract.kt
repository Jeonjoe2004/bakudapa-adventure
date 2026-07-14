package com.bakudapa.adventure.feature.feed.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.feed.domain.model.Comment

data class CommentsState(
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val commentInput: String = "",
    val isSending: Boolean = false,
    val error: String? = null
) : UiState

sealed class CommentsEvent : UiEvent {
    data class LoadComments(val postId: String) : CommentsEvent()
    data class OnInputChanged(val text: String) : CommentsEvent()
    data class SendComment(val postId: String) : CommentsEvent()
}

sealed class CommentsEffect : UiEffect {
    data class ShowError(val message: String) : CommentsEffect()
    object CommentSent : CommentsEffect()
}
