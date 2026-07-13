package com.bakudapa.adventure.feature.auth.ui.register

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.usecase.SignUpUseCase
import com.bakudapa.adventure.feature.auth.utils.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) : UiState

sealed class RegisterEvent : UiEvent {
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val password: String) : RegisterEvent()
    object RegisterClicked : RegisterEvent()
    object LoginClicked : RegisterEvent()
}

sealed class RegisterEffect : UiEffect {
    object NavigateToVerifyEmail : RegisterEffect()
    object NavigateToLogin : RegisterEffect()
    data class ShowError(val message: String) : RegisterEffect()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<RegisterState, RegisterEvent, RegisterEffect>(RegisterState()) {

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> setState { it.copy(email = event.email, emailError = null) }
            is RegisterEvent.PasswordChanged -> setState { it.copy(password = event.password, passwordError = null) }
            is RegisterEvent.ConfirmPasswordChanged -> setState { it.copy(confirmPassword = event.password, confirmPasswordError = null) }
            RegisterEvent.RegisterClicked -> register()
            RegisterEvent.LoginClicked -> sendEffect(RegisterEffect.NavigateToLogin)
        }
    }

    private fun register() {
        val currentState = uiState.value
        val isEmailValid = AuthValidator.isValidEmail(currentState.email)
        val isPasswordValid = AuthValidator.isValidPassword(currentState.password)
        val passwordsMatch = currentState.password == currentState.confirmPassword

        if (!isEmailValid || !isPasswordValid || !passwordsMatch) {
            setState { 
                it.copy(
                    emailError = if (!isEmailValid) "Invalid email" else null,
                    passwordError = if (!isPasswordValid) "Password too short" else null,
                    confirmPasswordError = if (!passwordsMatch) "Passwords don't match" else null
                )
            }
            return
        }

        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            when (val result = signUpUseCase(currentState.email, currentState.password)) {
                is DataResult.Success -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(RegisterEffect.NavigateToVerifyEmail)
                }
                is DataResult.Error -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(RegisterEffect.ShowError(result.exception.message ?: "Registration failed"))
                }
                DataResult.Loading -> {}
            }
        }
    }
}
