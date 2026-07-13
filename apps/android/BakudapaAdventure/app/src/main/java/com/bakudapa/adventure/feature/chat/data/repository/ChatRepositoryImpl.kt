package com.bakudapa.adventure.feature.chat.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.chat.domain.model.*
import com.bakudapa.adventure.feature.chat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : ChatRepository {

    override fun getChatRooms(): Flow<DataResult<List<ChatRoom>>> = callbackFlow {
        trySend(DataResult.Loading)
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        
        val listener = firestoreManager.getCollection("chat_rooms")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessage.timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val rooms = snapshot?.documents?.mapNotNull { it.toObject(ChatRoom::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(rooms))
            }
        awaitClose { listener.remove() }
    }

    override fun getMessages(roomId: String): Flow<DataResult<List<Message>>> = callbackFlow {
        trySend(DataResult.Loading)
        val listener = firestoreManager.getCollection("chat_rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResult.Error(error))
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java)?.copy(id = it.id) } ?: emptyList()
                trySend(DataResult.Success(messages))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(
        roomId: String,
        content: String,
        mediaType: MessageMediaType?,
        mediaUrl: String?,
        location: MessageLocation?
    ): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not authenticated")
            val message = Message(
                senderId = user.uid,
                senderName = user.displayName ?: "Anonymous",
                content = content,
                mediaType = mediaType,
                mediaUrl = mediaUrl,
                location = location,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENT
            )
            
            val roomRef = firestoreManager.getCollection("chat_rooms").document(roomId)
            roomRef.collection("messages").add(message).await()
            roomRef.update("lastMessage", message).await()
            
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun setTypingStatus(roomId: String, isTyping: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val roomRef = firestoreManager.getCollection("chat_rooms").document(roomId)
        if (isTyping) {
            roomRef.update("typingParticipants", FieldValue.arrayUnion(userId))
        } else {
            roomRef.update("typingParticipants", FieldValue.arrayRemove(userId))
        }
    }

    override fun getUserStatus(userId: String): Flow<DataResult<ChatUser>> = callbackFlow {
        val listener = firestoreManager.getCollection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val user = snapshot?.toObject(ChatUser::class.java)?.copy(id = snapshot.id)
                if (user != null) trySend(DataResult.Success(user))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(roomId: String, messageId: String) {
        firestoreManager.getCollection("chat_rooms").document(roomId)
            .collection("messages").document(messageId)
            .update("status", MessageStatus.READ)
    }

    override suspend fun createGroupChat(name: String, participantIds: List<String>): DataResult<String> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not authenticated")
            val allParticipants = participantIds + userId
            val room = ChatRoom(
                name = name,
                type = ChatType.GROUP,
                participants = allParticipants
            )
            val doc = firestoreManager.getCollection("chat_rooms").add(room).await()
            DataResult.Success(doc.id)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
