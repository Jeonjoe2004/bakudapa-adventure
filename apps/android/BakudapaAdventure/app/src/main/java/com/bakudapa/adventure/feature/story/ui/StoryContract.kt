package com.bakudapa.adventure.feature.story.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.story.domain.model.Story
import com.bakudapa.adventure.feature.story.domain.model.StoryUser

data class StoryBarState(
    val isLoading: Boolean = false,
    val storyUsers: List<StoryUser> = emptyList(),
    val error: String? = null
) : UiState

data class StoryViewerState(
    val isLoading: Boolean = false,
    val userId: String = "",
    val username: String = "",
    val userPhotoUrl: String? = null,
    val stories: List<Story> = emptyList(),
    val currentIndex: Int = 0,
    val isPaused: Boolean = false,
    val error: String? = null
) : UiState

sealed class StoryBarEvent : UiEvent {
    object LoadStoryUsers : StoryBarEvent()
    data class OnUserStoryClicked(val userId: String) : StoryBarEvent()
}

sealed class StoryViewerEvent : UiEvent {
    data class LoadStories(val userId: String) : StoryViewerEvent()
    object OnNextStory : StoryViewerEvent()
    object OnPreviousStory : StoryViewerEvent()
    object OnTogglePause : StoryViewerEvent()
    object OnClose : StoryViewerEvent()
    data class OnProgressComplete(val index: Int) : StoryViewerEvent()
}

sealed class StoryBarEffect : UiEffect {
    data class NavigateToStoryViewer(val userId: String) : StoryBarEffect()
    data class ShowError(val message: String) : StoryBarEffect()
}

sealed class StoryViewerEffect : UiEffect {
    object CloseViewer : StoryViewerEffect()
    data class ShowError(val message: String) : StoryViewerEffect()
}
