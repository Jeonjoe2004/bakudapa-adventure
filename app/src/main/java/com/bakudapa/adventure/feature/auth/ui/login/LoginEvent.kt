package com.bakudapa.adventure.feature.auth.ui.login

sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    object OnLoginClicked : LoginEvent()
}
