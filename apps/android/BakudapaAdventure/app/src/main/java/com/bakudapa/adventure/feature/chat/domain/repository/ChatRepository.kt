package com.bakudapa.adventure.feature.chat.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.chat.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatRooms(): Flow<DataResult<List<ChatRoom>>>
    
    fun getMessages(roomId: String): Flow<DataResult<List<Message>>>
    
    suspend fun sendMessage(
        roomId: String,
        content: String,
        mediaType: MessageMediaType? = null,
        mediaUrl: String? = null,
        location: MessageLocation? = null
    ): DataResult<Unit>
    
    suspend fun setTypingStatus(roomId: String, isTyping: Boolean)
    
    fun getUserStatus(userId: String): Flow<DataResult<ChatUser>>
    
    suspend fun markAsRead(roomId: String, messageId: String)
    
    suspend fun createGroupChat(name: String, participantIds: List<String>): DataResult<String>
}
