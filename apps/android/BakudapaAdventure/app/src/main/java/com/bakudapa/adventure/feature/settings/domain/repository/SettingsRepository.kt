package com.bakudapa.adventure.feature.settings.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.settings.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<DataResult<AppSettings>>
    suspend fun updateSettings(settings: AppSettings): DataResult<Unit>
    suspend fun toggleDarkMode(enabled: Boolean): DataResult<Unit>
    suspend fun toggleMetricUnit(enabled: Boolean): DataResult<Unit>
    suspend fun toggleNotifications(enabled: Boolean): DataResult<Unit>
}
