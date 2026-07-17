package com.bakudapa.adventure.feature.profile.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.profile.domain.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: ProfileRepository
) : BaseViewModel<ProfileState, ProfileEvent, ProfileEffect>(ProfileState()) {

    private var searchJob: Job? = null

    init {
        loadProfile()
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.OnTabSelected -> setState { it.copy(selectedTab = event.index) }
            ProfileEvent.OnEditProfileClicked -> sendEffect(ProfileEffect.NavigateToEditProfile)
            ProfileEvent.OnSignOutClicked -> signOut()
            ProfileEvent.OnFollowersClicked -> sendEffect(ProfileEffect.NavigateToFollowers)
            ProfileEvent.OnFollowingClicked -> sendEffect(ProfileEffect.NavigateToFollowing)
            is ProfileEvent.OnSaveProfile -> {
                viewModelScope.launch {
                    repository.updateProfile(event.name, event.username, event.bio, event.website, event.photoUrl).also { result ->
                        when (result) {
                            is DataResult.Success -> {
                                sendEffect(ProfileEffect.ShowToast("Profile updated"))
                                loadProfile()
                            }
                            is DataResult.Error -> sendEffect(ProfileEffect.ShowError(result.exception.message ?: "Update failed"))
                            DataResult.Loading -> {}
                        }
                    }
                }
            }
            is ProfileEvent.OnSearchQueryChanged -> handleSearchQuery(event.query)
            is ProfileEvent.OnUserClicked -> sendEffect(ProfileEffect.NavigateToUserProfile(event.userId))
        }
    }

    private fun handleSearchQuery(query: String) {
        setState { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            setState { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            setState { it.copy(isSearching = true) }
            when (val result = repository.searchUsers(query)) {
                is DataResult.Success -> setState { it.copy(searchResults = result.data, isSearching = false) }
                is DataResult.Error -> setState { it.copy(isSearching = false) }
                DataResult.Loading -> {}
            }
        }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }

            launch {
                repository.getUserProfile(userId).collectLatest { result ->
                    when (result) {
                        is DataResult.Success -> setState { it.copy(profile = result.data, isLoading = false) }
                        is DataResult.Error -> setState { it.copy(error = result.exception.message, isLoading = false) }
                        DataResult.Loading -> setState { it.copy(isLoading = true) }
                    }
                }
            }
            launch {
                repository.getMyPosts(userId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(myPosts = result.data) }
                }
            }
            launch {
                repository.getMyRoutes(userId).collectLatest { result ->
                    if (result is DataResult.Success) setState { it.copy(myRoutes = result.data) }
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

    private fun signOut() {
        auth.signOut()
        sendEffect(ProfileEffect.NavigateToAuth)
    }
}
