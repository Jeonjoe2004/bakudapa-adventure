package com.bakudapa.adventure.feature.profile.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: ProfileRepository
) : BaseViewModel<UserProfileState, UserProfileEvent, UserProfileEffect>(UserProfileState()) {

    override fun onEvent(event: UserProfileEvent) {
        when (event) {
            is UserProfileEvent.LoadProfile -> loadProfile(event.userId)
            UserProfileEvent.OnFollowClicked -> followUser()
            UserProfileEvent.OnUnfollowClicked -> unfollowUser()
        }
    }

    private var currentUserId: String = ""

    private fun loadProfile(userId: String) {
        currentUserId = userId
        if (auth.currentUser?.uid == userId) return // don't show follow for self
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            launch {
                repository.getUserProfile(userId).collectLatest { result ->
                    if (result is DataResult.Success) {
                        setState { it.copy(profile = result.data, isLoading = false) }
                    }
                }
            }
            launch {
                repository.isFollowing(userId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(isFollowing = result.data) }
                }
            }
            launch {
                repository.getFollowersCount(userId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(followersCount = result.data) }
                }
            }
            launch {
                repository.getFollowingCount(userId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(followingCount = result.data) }
                }
            }
        }
    }

    private fun followUser() {
        viewModelScope.launch {
            setState { it.copy(isFollowLoading = true) }
            when (repository.followUser(currentUserId)) {
                is DataResult.Success -> setState { it.copy(isFollowing = true, isFollowLoading = false) }
                else -> {
                    setState { it.copy(isFollowLoading = false) }
                    sendEffect(UserProfileEffect.ShowError("Gagal mengikuti"))
                }
            }
        }
    }

    private fun unfollowUser() {
        viewModelScope.launch {
            setState { it.copy(isFollowLoading = true) }
            when (repository.unfollowUser(currentUserId)) {
                is DataResult.Success -> setState { it.copy(isFollowing = false, isFollowLoading = false) }
                else -> {
                    setState { it.copy(isFollowLoading = false) }
                    sendEffect(UserProfileEffect.ShowError("Gagal unfollow"))
                }
            }
        }
    }
}
