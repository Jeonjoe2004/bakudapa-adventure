package com.bakudapa.adventure.feature.chat.domain.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val mediaUrl: String? = null,
    val mediaType: MessageMediaType? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val location: MessageLocation? = null
)

enum class MessageMediaType {
    IMAGE, VOICE, LOCATION
}

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

data class MessageLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)
