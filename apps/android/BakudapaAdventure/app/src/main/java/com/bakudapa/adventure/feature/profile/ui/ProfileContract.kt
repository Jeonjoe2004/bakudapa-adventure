package com.bakudapa.adventure.feature.profile.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val myPosts: List<Post> = emptyList(),
    val myRoutes: List<HikingRoute> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null
) : UiState

sealed class ProfileEvent : UiEvent {
    object LoadProfile : ProfileEvent()
    data class OnTabSelected(val index: Int) : ProfileEvent()
    object OnEditProfileClicked : ProfileEvent()
    object OnSignOutClicked : ProfileEvent()
}

sealed class ProfileEffect : UiEffect {
    object NavigateToEditProfile : ProfileEffect()
    object NavigateToAuth : ProfileEffect()
    data class ShowToast(val message: String) : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
}
