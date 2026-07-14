package com.bakudapa.adventure.feature.settings.domain.model

data class AppSettings(
    val isDarkMode: Boolean = false,
    val isMetricUnit: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val sosContactsEnabled: Boolean = true,
    val offlineMapsEnabled: Boolean = false,
    val autoTrackEnabled: Boolean = true
)
