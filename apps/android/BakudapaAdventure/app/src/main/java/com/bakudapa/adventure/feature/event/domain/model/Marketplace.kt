package com.bakudapa.adventure.feature.event.domain.model

data class MarketplaceItem(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val name: String = "",
    val description: String = "",
    val category: MarketplaceCategory = MarketplaceCategory.EQUIPMENT,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val condition: String = "NEW",
    val isPremium: Boolean = false,
    val createdAt: Long = 0L,
)

enum class MarketplaceCategory {
    EQUIPMENT, CLOTHING, FOOTWEAR, ACCESSORIES,
    TENTS, BACKPACKS, SLEEPING_BAGS, COOKING,
    NAVIGATION, LIGHTING, EMERGENCY, OTHER
}

data class BookingRequest(
    val id: String = "",
    val userId: String = "",
    val type: BookingType = BookingType.BASECAMP,
    val serviceName: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val mountainName: String = "",
    val checkInDate: Long = 0L,
    val checkOutDate: Long = 0L,
    val guests: Int = 1,
    val totalPrice: Double = 0.0,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String = "",
)

enum class BookingType {
    BASECAMP, GUIDE, PORTER, CAMPING_GROUND
}

enum class BookingStatus {
    PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, REFUNDED
}
