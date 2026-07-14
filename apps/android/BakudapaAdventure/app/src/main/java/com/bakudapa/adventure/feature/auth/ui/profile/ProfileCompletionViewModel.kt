package com.bakudapa.adventure.feature.auth.ui.profile

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import com.bakudapa.adventure.feature.auth.utils.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileCompletionState(
    val displayName: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null
) : UiState

sealed class ProfileCompletionEvent : UiEvent {
    data class DisplayNameChanged(val name: String) : ProfileCompletionEvent()
    object SubmitClicked : ProfileCompletionEvent()
}

sealed class ProfileCompletionEffect : UiEffect {
    object NavigateToHome : ProfileCompletionEffect()
    data class ShowError(val message: String) : ProfileCompletionEffect()
}

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<ProfileCompletionState, ProfileCompletionEvent, ProfileCompletionEffect>(ProfileCompletionState()) {

    override fun onEvent(event: ProfileCompletionEvent) {
        when (event) {
            is ProfileCompletionEvent.DisplayNameChanged -> setState { it.copy(displayName = event.name, nameError = null) }
            ProfileCompletionEvent.SubmitClicked -> submit()
        }
    }

    private fun submit() {
        val name = uiState.value.displayName
        if (!AuthValidator.isValidDisplayName(name)) {
            setState { it.copy(nameError = "Display name must be at least 3 characters") }
            return
        }

        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            when (val result = authRepository.completeProfile(name, null)) {
                is DataResult.Success -> sendEffect(ProfileCompletionEffect.NavigateToHome)
                is DataResult.Error -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(ProfileCompletionEffect.ShowError(result.exception.message ?: "Failed to update profile"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
