package com.bakudapa.adventure.feature.settings.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.settings.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val auth: FirebaseAuth
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>(SettingsState()) {

    init {
        loadSettings()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is SettingsEvent.ToggleMetricUnit -> toggleMetricUnit(event.enabled)
            is SettingsEvent.ToggleNotifications -> toggleNotifications(event.enabled)
            SettingsEvent.OnLogoutClicked -> signOut()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            repository.getSettings().collectLatest { result ->
                when (result) {
                    is DataResult.Success -> setState {
                        it.copy(isLoading = false, settings = result.data)
                    }
                    is DataResult.Error -> setState {
                        it.copy(isLoading = false, error = result.exception.message)
                    }
                    DataResult.Loading -> {}
                }
            }
        }
    }

    private fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            setState { it.copy(settings = it.settings.copy(isDarkMode = enabled)) }
            repository.toggleDarkMode(enabled)
        }
    }

    private fun toggleMetricUnit(enabled: Boolean) {
        viewModelScope.launch {
            setState { it.copy(settings = it.settings.copy(isMetricUnit = enabled)) }
            repository.toggleMetricUnit(enabled)
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            setState { it.copy(settings = it.settings.copy(notificationsEnabled = enabled)) }
            repository.toggleNotifications(enabled)
        }
    }

    private fun signOut() {
        auth.signOut()
        sendEffect(SettingsEffect.NavigateToAuth)
    }
}
