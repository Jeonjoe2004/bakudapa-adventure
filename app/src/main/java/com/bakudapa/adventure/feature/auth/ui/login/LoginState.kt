package com.bakudapa.adventure.feature.auth.ui.login

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
