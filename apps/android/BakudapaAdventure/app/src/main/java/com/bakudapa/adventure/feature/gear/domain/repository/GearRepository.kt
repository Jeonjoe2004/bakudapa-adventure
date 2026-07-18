package com.bakudapa.adventure.feature.gear.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.gear.domain.model.GearItem
import com.bakudapa.adventure.feature.gear.domain.model.GearPreset
import kotlinx.coroutines.flow.Flow

interface GearRepository {
    fun getGearPresets(): List<GearPreset>
    fun getSavedChecklist(mountainId: String): Flow<DataResult<List<GearItem>>>
    suspend fun saveChecklist(mountainId: String, items: List<GearItem>): DataResult<Unit>
    suspend fun toggleItem(mountainId: String, itemId: String): DataResult<Unit>
}
