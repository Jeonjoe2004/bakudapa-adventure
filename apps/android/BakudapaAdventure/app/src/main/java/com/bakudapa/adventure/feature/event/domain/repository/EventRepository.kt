package com.bakudapa.adventure.feature.event.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.event.domain.model.*
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(): Flow<DataResult<List<HikingEvent>>>
    fun getMyEvents(userId: String): Flow<DataResult<List<HikingEvent>>>
    suspend fun registerForEvent(eventId: String, ticketType: TicketType): DataResult<Unit>
    suspend fun cancelRegistration(eventId: String): DataResult<Unit>
}

interface MarketplaceRepository {
    fun getItems(category: MarketplaceCategory?): Flow<DataResult<List<MarketplaceItem>>>
    suspend fun addItem(item: MarketplaceItem): DataResult<Unit>
    suspend fun deleteItem(itemId: String): DataResult<Unit>
}

interface BookingRepository {
    suspend fun createBooking(request: BookingRequest): DataResult<Unit>
    fun getMyBookings(userId: String): Flow<DataResult<List<BookingRequest>>>
    suspend fun cancelBooking(bookingId: String): DataResult<Unit>
}
