package com.bakudapa.adventure.feature.auth.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashEffect {
    object NavigateToHome : SplashEffect()
    object NavigateToOnboarding : SplashEffect()
    object NavigateToLogin : SplashEffect()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            delay(2000) // Visual splash delay
            val user = authRepository.authUser.first()
            if (user != null) {
                if (user.isEmailVerified) {
                    _effect.emit(SplashEffect.NavigateToHome)
                } else {
                    _effect.emit(SplashEffect.NavigateToLogin)
                }
            } else {
                _effect.emit(SplashEffect.NavigateToOnboarding)
            }
        }
    }
}
