package com.bakudapa.adventure.feature.gear.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.gear.domain.repository.GearRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GearViewModel @Inject constructor(
    private val repository: GearRepository
) : BaseViewModel<GearState, GearEvent, GearEffect>(GearState()) {

    override fun onEvent(event: GearEvent) {
        when (event) {
            is GearEvent.LoadChecklist -> load(event.mountainId, event.mountainName)
            is GearEvent.SelectPreset -> selectPreset(event.index)
            is GearEvent.ToggleItem -> toggleItem(event.itemId)
            GearEvent.SaveChecklist -> save()
        }
    }

    private fun load(mountainId: String, mountainName: String) {
        setState { it.copy(mountainId = mountainId, mountainName = mountainName, presets = repository.getGearPresets()) }
        // Cek checklist tersimpan
        viewModelScope.launch {
            repository.getSavedChecklist(mountainId).collectLatest { result ->
                when (result) {
                    is DataResult.Success -> {
                        if (result.data.isNotEmpty()) {
                            setState { it.copy(items = result.data, isLoading = false).also { updateCounts() } }
                        } else {
                            // Pake preset pertama
                            applyPreset(0)
                        }
                    }
                    is DataResult.Error -> setState { it.copy(isLoading = false, error = result.exception.message) }
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun selectPreset(index: Int) {
        applyPreset(index)
    }

    private fun applyPreset(index: Int) {
        val presets = uiState.value.presets
        if (index in presets.indices) {
            val items = presets[index].items.map { it.copy(isChecked = false) }
            setState { it.copy(selectedPresetIndex = index, items = items, preset = presets[index]).also { updateCounts() } }
        }
    }

    private fun toggleItem(itemId: String) {
        val updated = uiState.value.items.map { if (it.id == itemId) it.copy(isChecked = !it.isChecked) else it }
        setState { it.copy(items = updated).also { updateCounts() } }
        // Auto-save
        viewModelScope.launch {
            repository.toggleItem(uiState.value.mountainId, itemId)
        }
    }

    private fun save() {
        viewModelScope.launch {
            repository.saveChecklist(uiState.value.mountainId, uiState.value.items).also {
                if (it is DataResult.Success) sendEffect(GearEffect.ShowToast("Checklist tersimpan"))
                else sendEffect(GearEffect.ShowToast("Gagal menyimpan"))
            }
        }
    }

    private fun updateCounts() {
        val items = uiState.value.items
        val total = items.size
        val checked = items.count { it.isChecked }
        setState { it.copy(checkedCount = checked, totalCount = total) }
    }
}
