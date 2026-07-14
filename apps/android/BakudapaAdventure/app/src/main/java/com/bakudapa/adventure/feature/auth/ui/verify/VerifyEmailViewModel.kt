package com.bakudapa.adventure.feature.auth.ui.verify

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerifyEmailState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false
) : UiState

sealed class VerifyEmailEvent : UiEvent {
    object ResendEmailClicked : VerifyEmailEvent()
    object CheckVerificationClicked : VerifyEmailEvent()
    object BackToLoginClicked : VerifyEmailEvent()
}

sealed class VerifyEmailEffect : UiEffect {
    object NavigateToProfileCompletion : VerifyEmailEffect()
    object NavigateToLogin : VerifyEmailEffect()
    data class ShowMessage(val message: String) : VerifyEmailEffect()
}

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<VerifyEmailState, VerifyEmailEvent, VerifyEmailEffect>(VerifyEmailState()) {

    init {
        viewModelScope.launch {
            val user = authRepository.authUser.first()
            user?.let { setState { it.copy(email = user.email) } }
            sendVerification()
        }
    }

    override fun onEvent(event: VerifyEmailEvent) {
        when (event) {
            VerifyEmailEvent.ResendEmailClicked -> sendVerification()
            VerifyEmailEvent.CheckVerificationClicked -> checkVerification()
            VerifyEmailEvent.BackToLoginClicked -> sendEffect(VerifyEmailEffect.NavigateToLogin)
        }
    }

    private fun sendVerification() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            authRepository.sendEmailVerification()
            setState { it.copy(isLoading = false) }
            sendEffect(VerifyEmailEffect.ShowMessage("Verification email sent!"))
        }
    }

    private fun checkVerification() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            when (val result = authRepository.reloadUser()) {
                is DataResult.Success -> {
                    if (result.data?.isEmailVerified == true) {
                        sendEffect(VerifyEmailEffect.NavigateToProfileCompletion)
                    } else {
                        sendEffect(VerifyEmailEffect.ShowMessage("Email not verified yet."))
                    }
                }
                is DataResult.Error -> sendEffect(VerifyEmailEffect.ShowMessage("Error checking verification."))
                DataResult.Loading -> {}
            }
            setState { it.copy(isLoading = false) }
        }
    }
}
