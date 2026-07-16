package com.bakudapa.adventure.feature.event.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.event.domain.model.*
import com.bakudapa.adventure.feature.event.domain.repository.BookingRepository
import com.bakudapa.adventure.feature.event.domain.repository.EventRepository
import com.bakudapa.adventure.feature.event.domain.repository.MarketplaceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : EventRepository, MarketplaceRepository, BookingRepository {

    override fun getEvents(): Flow<DataResult<List<HikingEvent>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("events")
            .orderBy("startDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val events = snap?.documents?.mapNotNull {
                    it.toObject(HikingEvent::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(DataResult.Success(events))
            }
        awaitClose { listener.remove() }
    }

    override fun getMyEvents(userId: String): Flow<DataResult<List<HikingEvent>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("event_participants")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val eventIds = snap?.documents?.mapNotNull { it.getString("eventId") } ?: emptyList()
                trySend(DataResult.Success(emptyList()))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun registerForEvent(eventId: String, ticketType: TicketType): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val participant = EventParticipant(
                userId = user.uid,
                userName = user.displayName ?: "",
                userPhotoUrl = user.photoUrl?.toString(),
                eventId = eventId,
                ticketType = ticketType,
            )
            firestoreManager.getCollection("event_participants").add(participant).await()
            firestoreManager.getCollection("events").document(eventId)
                .update("currentParticipants", com.google.firebase.firestore.FieldValue.increment(1)).await()
            DataResult.Success(Unit)
        } catch (e: Exception) { DataResult.Error(e) }
    }

    override suspend fun cancelRegistration(eventId: String): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val snap = firestoreManager.getCollection("event_participants")
                .whereEqualTo("userId", user.uid).whereEqualTo("eventId", eventId).get().await()
            snap.documents.forEach { it.reference.delete().await() }
            firestoreManager.getCollection("events").document(eventId)
                .update("currentParticipants", com.google.firebase.firestore.FieldValue.increment(-1)).await()
            DataResult.Success(Unit)
        } catch (e: Exception) { DataResult.Error(e) }
    }

    override fun getItems(category: MarketplaceCategory?): Flow<DataResult<List<MarketplaceItem>>> = callbackFlow {
        trySend(DataResult.Loading)
        val ref = firestoreManager.getCollection("marketplace")
        val query = if (category != null) ref.whereEqualTo("category", category.name)
                    else ref.orderBy("createdAt", Query.Direction.DESCENDING)
        val listener = query.addSnapshotListener { snap, error ->
            if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
            val items = snap?.documents?.mapNotNull { it.toObject(MarketplaceItem::class.java)?.copy(id = it.id) } ?: emptyList()
            trySend(DataResult.Success(items))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addItem(item: MarketplaceItem): DataResult<Unit> {
        return try { firestoreManager.getCollection("marketplace").add(item).await(); DataResult.Success(Unit) }
        catch (e: Exception) { DataResult.Error(e) }
    }

    override suspend fun deleteItem(itemId: String): DataResult<Unit> {
        return try { firestoreManager.getCollection("marketplace").document(itemId).delete().await(); DataResult.Success(Unit) }
        catch (e: Exception) { DataResult.Error(e) }
    }

    override suspend fun createBooking(request: BookingRequest): DataResult<Unit> {
        return try { firestoreManager.getCollection("bookings").add(request).await(); DataResult.Success(Unit) }
        catch (e: Exception) { DataResult.Error(e) }
    }

    override fun getMyBookings(userId: String): Flow<DataResult<List<BookingRequest>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("bookings")
            .whereEqualTo("userId", userId)
            .orderBy("checkInDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, error ->
                if (error != null) { trySend(DataResult.Error(error)); return@addSnapshotListener }
                val items = snap?.documents?.mapNotNull { it.toObject(BookingRequest::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(items))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun cancelBooking(bookingId: String): DataResult<Unit> {
        return try {
            firestoreManager.getCollection("bookings").document(bookingId)
                .update("status", BookingStatus.CANCELLED.name).await()
            DataResult.Success(Unit)
        } catch (e: Exception) { DataResult.Error(e) }
    }
}
