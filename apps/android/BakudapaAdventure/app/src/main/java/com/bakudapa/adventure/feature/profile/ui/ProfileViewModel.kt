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
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: ProfileRepository
) : BaseViewModel<ProfileState, ProfileEvent, ProfileEffect>(ProfileState()) {

    init {
        loadProfile()
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.OnTabSelected -> setState { it.copy(selectedTab = event.index) }
            ProfileEvent.OnEditProfileClicked -> sendEffect(ProfileEffect.NavigateToEditProfile)
            ProfileEvent.OnSignOutClicked -> signOut()
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
        }
    }

    private fun signOut() {
        auth.signOut()
        sendEffect(ProfileEffect.NavigateToAuth)
    }
}
