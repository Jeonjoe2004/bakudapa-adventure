package com.bakudapa.adventure.feature.emergency.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.emergency.domain.model.EmergencyContact
import com.bakudapa.adventure.feature.emergency.domain.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val repository: EmergencyRepository
) : BaseViewModel<EmergencyState, EmergencyEvent, EmergencyEffect>(EmergencyState()) {

    init {
        loadData()
    }

    override fun onEvent(event: EmergencyEvent) {
        when (event) {
            EmergencyEvent.LoadData -> loadData()
            EmergencyEvent.TriggerSOS -> triggerSOS()
            is EmergencyEvent.ResolveSOS -> resolveSOS(event.id)
            is EmergencyEvent.AddContact -> addContact(event.name, event.phone, event.relation)
            is EmergencyEvent.DeleteContact -> deleteContact(event.id)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { it.copy(isLoading = true) }
            
            launch {
                repository.getEmergencyContacts().collectLatest { result ->
                    if (result is DataResult.Success) {
                        setState { it.copy(contacts = result.data) }
                    }
                }
            }
            
            launch {
                repository.getActiveSOS().collectLatest { result ->
                    if (result is DataResult.Success) {
                        setState { it.copy(activeAlerts = result.data, isLoading = false) }
                    }
                }
            }
        }
    }

    private fun triggerSOS() {
        viewModelScope.launch {
            // In real app, get current GPS location first
            val result = repository.triggerSOSWithAutoLocation("SOS! I need help!")
            if (result is DataResult.Success) {
                sendEffect(EmergencyEffect.ShowToast("SOS Alert Triggered!"))
            } else if (result is DataResult.Error) {
                sendEffect(EmergencyEffect.ShowToast("Failed to trigger SOS: ${result.exception?.message}"))
            }
        }
    }

    private fun resolveSOS(id: String) {
        viewModelScope.launch {
            repository.resolveSOS(id)
        }
    }

    private fun addContact(name: String, phone: String, relation: String) {
        viewModelScope.launch {
            val contact = EmergencyContact(name = name, phoneNumber = phone, relation = relation)
            repository.addEmergencyContact(contact)
        }
    }

    private fun deleteContact(id: String) {
        viewModelScope.launch {
            repository.deleteEmergencyContact(id)
        }
    }
}
