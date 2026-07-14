package com.bakudapa.adventure.feature.settings.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.settings.domain.model.AppSettings
import com.bakudapa.adventure.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val METRIC_UNIT = booleanPreferencesKey("metric_unit")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
        val SOS_CONTACTS = booleanPreferencesKey("sos_contacts")
        val OFFLINE_MAPS = booleanPreferencesKey("offline_maps")
        val AUTO_TRACK = booleanPreferencesKey("auto_track")
    }

    override fun getSettings(): Flow<DataResult<AppSettings>> =
        context.dataStore.data.map { prefs ->
            DataResult.Success(
                AppSettings(
                    isDarkMode = prefs[Keys.DARK_MODE] ?: false,
                    isMetricUnit = prefs[Keys.METRIC_UNIT] ?: true,
                    notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
                    sosContactsEnabled = prefs[Keys.SOS_CONTACTS] ?: true,
                    offlineMapsEnabled = prefs[Keys.OFFLINE_MAPS] ?: false,
                    autoTrackEnabled = prefs[Keys.AUTO_TRACK] ?: true
                )
            )
        }

    override suspend fun updateSettings(settings: AppSettings): DataResult<Unit> = try {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = settings.isDarkMode
            prefs[Keys.METRIC_UNIT] = settings.isMetricUnit
            prefs[Keys.NOTIFICATIONS] = settings.notificationsEnabled
            prefs[Keys.SOS_CONTACTS] = settings.sosContactsEnabled
            prefs[Keys.OFFLINE_MAPS] = settings.offlineMapsEnabled
            prefs[Keys.AUTO_TRACK] = settings.autoTrackEnabled
        }
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }

    override suspend fun toggleDarkMode(enabled: Boolean): DataResult<Unit> = try {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }

    override suspend fun toggleMetricUnit(enabled: Boolean): DataResult<Unit> = try {
        context.dataStore.edit { it[Keys.METRIC_UNIT] = enabled }
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }

    override suspend fun toggleNotifications(enabled: Boolean): DataResult<Unit> = try {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
        DataResult.Success(Unit)
    } catch (e: Exception) {
        DataResult.Error(e)
    }
}
