package com.bakudapa.adventure.feature.emergency.ui

import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.emergency.domain.model.EmergencyContact
import com.bakudapa.adventure.feature.emergency.domain.model.SOSAlert

data class EmergencyState(
    val isLoading: Boolean = false,
    val contacts: List<EmergencyContact> = emptyList(),
    val activeAlerts: List<SOSAlert> = emptyList(),
    val isSOSActive: Boolean = false,
    val error: String? = null
) : UiState

sealed class EmergencyEvent : UiEvent {
    object LoadData : EmergencyEvent()
    object TriggerSOS : EmergencyEvent()
    data class ResolveSOS(val id: String) : EmergencyEvent()
    data class AddContact(val name: String, val phone: String, val relation: String) : EmergencyEvent()
    data class DeleteContact(val id: String) : EmergencyEvent()
}

sealed class EmergencyEffect : UiEffect {
    data class ShowToast(val message: String) : EmergencyEffect()
}
