package com.bakudapa.adventure.feature.chat.domain.model

data class ChatUser(
    val id: String = "",
    val name: String = "",
    val photoUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = 0
)
