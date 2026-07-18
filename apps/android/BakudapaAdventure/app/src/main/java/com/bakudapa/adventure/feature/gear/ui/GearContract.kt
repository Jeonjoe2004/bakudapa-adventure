package com.bakudapa.adventure.feature.gear.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.gear.domain.model.GearCategory
import com.bakudapa.adventure.feature.gear.domain.model.GearItem
import com.bakudapa.adventure.feature.gear.domain.model.GearPreset

data class GearState(
    val isLoading: Boolean = false,
    val preset: GearPreset? = null,
    val items: List<GearItem> = emptyList(),
    val presets: List<GearPreset> = emptyList(),
    val selectedPresetIndex: Int = 0,
    val checkedCount: Int = 0,
    val totalCount: Int = 0,
    val mountainId: String = "",
    val mountainName: String = "",
    val error: String? = null
) : UiState

sealed class GearEvent : UiEvent {
    data class LoadChecklist(val mountainId: String, val mountainName: String) : GearEvent()
    data class SelectPreset(val index: Int) : GearEvent()
    data class ToggleItem(val itemId: String) : GearEvent()
    object SaveChecklist : GearEvent()
}

sealed class GearEffect : UiEffect {
    data class ShowToast(val message: String) : GearEffect()
}
