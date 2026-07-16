package com.bakudapa.adventure.feature.event.domain.model

data class HikingEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val organizerName: String = "",
    val organizerId: String = "",
    val mountainName: String = "",
    val trailId: String? = null,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val registrationDeadline: Long = 0L,
    val maxParticipants: Int = 0,
    val currentParticipants: Int = 0,
    val price: Double = 0.0,
    val difficulty: String = "MODERATE",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val status: EventStatus = EventStatus.UPCOMING,
    val includes: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val contactPhone: String = "",
)

enum class EventStatus {
    UPCOMING, ONGOING, COMPLETED, CANCELLED,
    BOOKING_OPEN, BOOKING_CLOSED, FULL
}

data class EventParticipant(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val eventId: String = "",
    val registrationDate: Long = 0L,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val ticketType: TicketType = TicketType.GENERAL,
)

enum class PaymentStatus {
    UNPAID, PAID, REFUNDED, CANCELLED
}

enum class TicketType {
    GENERAL, VIP, GUIDE, PORTER
}
