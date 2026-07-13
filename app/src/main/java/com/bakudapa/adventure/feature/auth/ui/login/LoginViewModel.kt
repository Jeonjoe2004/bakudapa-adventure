package com.bakudapa.adventure.feature.auth.ui.login

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.ui.BaseViewModel
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginState, LoginEvent>(LoginState()) {

    private var email = ""
    private var password = ""

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> email = event.email
            is LoginEvent.OnPasswordChanged -> password = event.password
            LoginEvent.OnLoginClicked -> login()
        }
    }

    private fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Implementation...
        }
    }
}
