package com.bakudapa.adventure.feature.profile.ui

import android.net.Uri
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.profile.domain.model.UserProfile

data class UserProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val isFollowing: Boolean = false,
    val isFollowLoading: Boolean = false,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val error: String? = null
) : UiState

sealed class UserProfileEvent : UiEvent {
    data class LoadProfile(val userId: String) : UserProfileEvent()
    object OnFollowClicked : UserProfileEvent()
    object OnUnfollowClicked : UserProfileEvent()
}

sealed class UserProfileEffect : UiEffect {
    data class ShowError(val message: String) : UserProfileEffect()
}
