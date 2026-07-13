package com.bakudapa.adventure.feature.auth.ui.forgot

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

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null
) : UiState

sealed class ForgotPasswordEvent : UiEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    object SubmitClicked : ForgotPasswordEvent()
    object BackToLoginClicked : ForgotPasswordEvent()
}

sealed class ForgotPasswordEffect : UiEffect {
    object NavigateBack : ForgotPasswordEffect()
    data class ShowSuccess(val message: String) : ForgotPasswordEffect()
    data class ShowError(val message: String) : ForgotPasswordEffect()
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent, ForgotPasswordEffect>(ForgotPasswordState()) {

    override fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> setState { it.copy(email = event.email, emailError = null) }
            ForgotPasswordEvent.SubmitClicked -> submit()
            ForgotPasswordEvent.BackToLoginClicked -> sendEffect(ForgotPasswordEffect.NavigateBack)
        }
    }

    private fun submit() {
        val email = uiState.value.email
        if (!AuthValidator.isValidEmail(email)) {
            setState { it.copy(emailError = "Invalid email") }
            return
        }

        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is DataResult.Success -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(ForgotPasswordEffect.ShowSuccess("Reset email sent!"))
                }
                is DataResult.Error -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(ForgotPasswordEffect.ShowError(result.exception.message ?: "Failed to send reset email"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
