package com.bakudapa.adventure.feature.chat.domain.model

data class ChatRoom(
    val id: String = "",
    val name: String = "",
    val iconUrl: String? = null,
    val type: ChatType = ChatType.PRIVATE,
    val participants: List<String> = emptyList(),
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val typingParticipants: List<String> = emptyList()
)

enum class ChatType {
    PRIVATE, GROUP
}
