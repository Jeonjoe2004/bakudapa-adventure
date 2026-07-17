package com.bakudapa.adventure.feature.profile.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.profile.domain.model.FollowUser
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val myPosts: List<Post> = emptyList(),
    val myRoutes: List<HikingRoute> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,

    // Search users
    val searchQuery: String = "",
    val searchResults: List<FollowUser> = emptyList(),
    val isSearching: Boolean = false
) : UiState

sealed class ProfileEvent : UiEvent {
    object LoadProfile : ProfileEvent()
    data class OnTabSelected(val index: Int) : ProfileEvent()
    object OnEditProfileClicked : ProfileEvent()
    object OnSignOutClicked : ProfileEvent()
    object OnFollowersClicked : ProfileEvent()
    object OnFollowingClicked : ProfileEvent()
    data class OnSaveProfile(val name: String, val username: String, val bio: String, val website: String, val photoUrl: String?) : ProfileEvent()

    // Search
    data class OnSearchQueryChanged(val query: String) : ProfileEvent()
    data class OnUserClicked(val userId: String) : ProfileEvent()
}

sealed class ProfileEffect : UiEffect {
    object NavigateToEditProfile : ProfileEffect()
    object NavigateToAuth : ProfileEffect()
    data class ShowToast(val message: String) : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
    object NavigateToFollowers : ProfileEffect()
    object NavigateToFollowing : ProfileEffect()
    data class NavigateToUserProfile(val userId: String) : ProfileEffect()
}
