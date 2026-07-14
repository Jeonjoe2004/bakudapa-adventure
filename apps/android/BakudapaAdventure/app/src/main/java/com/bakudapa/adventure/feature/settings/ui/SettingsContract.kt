package com.bakudapa.adventure.feature.settings.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.settings.domain.model.AppSettings

data class SettingsState(
    val isLoading: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val error: String? = null
) : UiState

sealed class SettingsEvent : UiEvent {
    object LoadSettings : SettingsEvent()
    data class ToggleDarkMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleMetricUnit(val enabled: Boolean) : SettingsEvent()
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent()
    object OnLogoutClicked : SettingsEvent()
}

sealed class SettingsEffect : UiEffect {
    data class ShowToast(val message: String) : SettingsEffect()
    object NavigateToAuth : SettingsEffect()
}
