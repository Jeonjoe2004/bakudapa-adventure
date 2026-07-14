package com.bakudapa.adventure.feature.emergency.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.emergency.domain.model.EmergencyContact
import com.bakudapa.adventure.feature.emergency.domain.model.SOSAlert
import kotlinx.coroutines.flow.Flow

interface EmergencyRepository {
    suspend fun triggerSOS(latitude: Double, longitude: Double, message: String): DataResult<Unit>
    suspend fun resolveSOS(sosId: String): DataResult<Unit>
    fun getActiveSOS(): Flow<DataResult<List<SOSAlert>>>
    
    fun getEmergencyContacts(): Flow<DataResult<List<EmergencyContact>>>
    suspend fun addEmergencyContact(contact: EmergencyContact): DataResult<Unit>
    suspend fun deleteEmergencyContact(contactId: String): DataResult<Unit>
}
