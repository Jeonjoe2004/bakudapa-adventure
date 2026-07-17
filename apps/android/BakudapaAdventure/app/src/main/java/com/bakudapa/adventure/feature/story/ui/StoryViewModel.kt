package com.bakudapa.adventure.feature.story.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.story.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryBarViewModel @Inject constructor(
    private val repository: StoryRepository
) : BaseViewModel<StoryBarState, StoryBarEvent, StoryBarEffect>(StoryBarState()) {

    init { load() }

    override fun onEvent(event: StoryBarEvent) {
        when (event) {
            StoryBarEvent.LoadStoryUsers -> load()
            is StoryBarEvent.OnUserStoryClicked -> {
                sendEffect(StoryBarEffect.NavigateToStoryViewer(event.userId))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            repository.getActiveStories().collect { result ->
                when (result) {
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                    is DataResult.Success -> setState {
                        it.copy(isLoading = false, storyUsers = result.data, error = null)
                    }
                    is DataResult.Error -> setState {
                        it.copy(isLoading = false, error = result.exception.message)
                    }
                }
            }
        }
    }
}

@HiltViewModel
class StoryViewerViewModel @Inject constructor(
    private val repository: StoryRepository
) : BaseViewModel<StoryViewerState, StoryViewerEvent, StoryViewerEffect>(StoryViewerState()) {

    override fun onEvent(event: StoryViewerEvent) {
        when (event) {
            is StoryViewerEvent.LoadStories -> loadStories(event.userId)
            StoryViewerEvent.OnNextStory -> nextStory()
            StoryViewerEvent.OnPreviousStory -> previousStory()
            StoryViewerEvent.OnTogglePause -> setState { it.copy(isPaused = !it.isPaused) }
            StoryViewerEvent.OnClose -> sendEffect(StoryViewerEffect.CloseViewer)
            is StoryViewerEvent.OnProgressComplete -> handleProgressComplete(event.index)
        }
    }

    private fun loadStories(userId: String) {
        viewModelScope.launch {
            setState { it.copy(isLoading = true, userId = userId) }
            repository.getUserStories(userId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        val stories = result.data
                        if (stories.isNotEmpty()) {
                            setState {
                                it.copy(
                                    isLoading = false,
                                    stories = stories,
                                    username = stories.first().username,
                                    userPhotoUrl = stories.first().userPhotoUrl,
                                    currentIndex = 0
                                )
                            }
                            // Tandai viewed
                            repository.markViewed(stories.first().id)
                        }
                    }
                    is DataResult.Error -> setState { it.copy(isLoading = false, error = result.exception.message) }
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun nextStory() {
        val current = uiState.value.currentIndex
        val total = uiState.value.stories.size
        if (current + 1 < total) {
            val newIndex = current + 1
            setState { it.copy(currentIndex = newIndex) }
            viewModelScope.launch {
                repository.markViewed(uiState.value.stories[newIndex].id)
            }
        } else {
            sendEffect(StoryViewerEffect.CloseViewer)
        }
    }

    private fun previousStory() {
        val current = uiState.value.currentIndex
        if (current > 0) setState { it.copy(currentIndex = current - 1) }
    }

    private fun handleProgressComplete(index: Int) {
        if (index == uiState.value.currentIndex) nextStory()
    }
}
