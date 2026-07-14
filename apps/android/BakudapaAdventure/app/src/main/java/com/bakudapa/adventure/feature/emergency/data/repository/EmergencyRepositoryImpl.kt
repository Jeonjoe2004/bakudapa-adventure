package com.bakudapa.adventure.feature.emergency.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.emergency.domain.model.EmergencyContact
import com.bakudapa.adventure.feature.emergency.domain.model.SOSAlert
import com.bakudapa.adventure.feature.emergency.domain.repository.EmergencyRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : EmergencyRepository {

    override suspend fun triggerSOS(latitude: Double, longitude: Double, message: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            val sos = SOSAlert(
                userId = user.uid,
                userName = user.displayName ?: "Adventurer",
                latitude = latitude,
                longitude = longitude,
                message = message,
                isActive = true
            )
            firestoreManager.getCollection("sos_alerts").add(sos).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun resolveSOS(sosId: String): DataResult<Unit> {
        return try {
            firestoreManager.getCollection("sos_alerts").document(sosId).update("isActive", false).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override fun getActiveSOS(): Flow<DataResult<List<SOSAlert>>> = callbackFlow {
        val listener = firestoreManager.getCollection("sos_alerts")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val alerts = snapshot?.documents?.mapNotNull { it.toObject(SOSAlert::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(alerts))
            }
        awaitClose { listener.remove() }
    }

    override fun getEmergencyContacts(): Flow<DataResult<List<EmergencyContact>>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = firestoreManager.getCollection("users").document(userId).collection("emergency_contacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val contacts = snapshot?.documents?.mapNotNull { it.toObject(EmergencyContact::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(contacts))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            firestoreManager.getCollection("users").document(userId).collection("emergency_contacts").add(contact).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun deleteEmergencyContact(contactId: String): DataResult<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            firestoreManager.getCollection("users").document(userId).collection("emergency_contacts").document(contactId).delete().await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
