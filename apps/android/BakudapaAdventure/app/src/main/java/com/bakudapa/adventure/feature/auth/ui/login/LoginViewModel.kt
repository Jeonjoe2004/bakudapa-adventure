package com.bakudapa.adventure.feature.auth.ui.login

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.usecase.SignInUseCase
import com.bakudapa.adventure.feature.auth.utils.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null
) : UiState

sealed class LoginEvent : UiEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object LoginClicked : LoginEvent()
    object ForgotPasswordClicked : LoginEvent()
    object RegisterClicked : LoginEvent()
}

sealed class LoginEffect : UiEffect {
    object NavigateToHome : LoginEffect()
    object NavigateToRegister : LoginEffect()
    object NavigateToForgotPassword : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : BaseViewModel<LoginState, LoginEvent, LoginEffect>(LoginState()) {

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> setState { it.copy(email = event.email, emailError = null) }
            is LoginEvent.PasswordChanged -> setState { it.copy(password = event.password, passwordError = null) }
            LoginEvent.LoginClicked -> login()
            LoginEvent.RegisterClicked -> sendEffect(LoginEffect.NavigateToRegister)
            LoginEvent.ForgotPasswordClicked -> sendEffect(LoginEffect.NavigateToForgotPassword)
        }
    }

    private fun login() {
        val currentState = uiState.value
        val isEmailValid = AuthValidator.isValidEmail(currentState.email)
        val isPasswordValid = AuthValidator.isValidPassword(currentState.password)

        if (!isEmailValid || !isPasswordValid) {
            setState { 
                it.copy(
                    emailError = if (!isEmailValid) "Invalid email" else null,
                    passwordError = if (!isPasswordValid) "Password must be at least 6 characters" else null
                )
            }
            return
        }

        viewModelScope.launch {
            setState { it.copy(isLoading = true, generalError = null) }
            when (val result = signInUseCase(currentState.email, currentState.password)) {
                is DataResult.Success -> {
                    setState { it.copy(isLoading = false) }
                    sendEffect(LoginEffect.NavigateToHome)
                }
                is DataResult.Error -> {
                    setState { it.copy(isLoading = false, generalError = result.exception.message) }
                    sendEffect(LoginEffect.ShowError(result.exception.message ?: "Login failed"))
                }
                DataResult.Loading -> { /* Handled by initial isLoading = true */ }
            }
        }
    }
}
