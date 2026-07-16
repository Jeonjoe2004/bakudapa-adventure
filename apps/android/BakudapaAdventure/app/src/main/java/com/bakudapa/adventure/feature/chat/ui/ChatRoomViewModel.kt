package com.bakudapa.adventure.feature.chat.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.chat.domain.model.MessageMediaType
import com.bakudapa.adventure.feature.chat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: ChatRepository
) : BaseViewModel<ChatRoomState, ChatRoomEvent, ChatRoomEffect>(ChatRoomState()) {

    private var currentRoomId: String? = null
    private var typingJob: Job? = null
    private val storageRef = Firebase.storage.reference

    override fun onEvent(event: ChatRoomEvent) {
        when (event) {
            is ChatRoomEvent.LoadMessages -> {
                currentRoomId = event.roomId
                loadMessages(event.roomId)
            }
            is ChatRoomEvent.OnMessageChanged -> {
                setState { it.copy(currentMessage = event.content) }
            }
            ChatRoomEvent.OnSendClicked -> sendMessage()
            is ChatRoomEvent.OnTyping -> handleTyping(event.isTyping)
            is ChatRoomEvent.OnMessageRead -> handleMessageRead(event.messageId)
            ChatRoomEvent.OnMediaClicked -> sendEffect(ChatRoomEffect.PickMedia)
            is ChatRoomEvent.OnMediaSelected -> uploadAndSendMedia(event.uri, event.isImage)
        }
    }

    fun sendMedia(uri: Uri, isImage: Boolean) {
        onEvent(ChatRoomEvent.OnMediaSelected(uri, isImage))
    }

    private fun uploadAndSendMedia(uri: Uri, isImage: Boolean) {
        val roomId = currentRoomId ?: return
        setState { it.copy(isUploadingMedia = true) }
        viewModelScope.launch {
            try {
                val fileName = "chat/${roomId}/${UUID.randomUUID()}"
                val downloadUrl = if (isImage) {
                    storageRef.child("$fileName.jpg").putFile(uri).await().storage.downloadUrl.await().toString()
                } else {
                    storageRef.child(fileName).putFile(uri).await().storage.downloadUrl.await().toString()
                }
                repository.sendMessage(
                    roomId = roomId,
                    content = if (isImage) "📷 Photo" else "📎 File",
                    mediaType = if (isImage) MessageMediaType.IMAGE else null,
                    mediaUrl = downloadUrl
                )
            } catch (e: Exception) {
                sendEffect(ChatRoomEffect.ShowError("Gagal upload media"))
            } finally {
                setState { it.copy(isUploadingMedia = false) }
            }
        }
    }

    private fun loadMessages(roomId: String) {
        viewModelScope.launch {
            repository.getMessages(roomId).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        setState { it.copy(messages = result.data, isLoading = false) }
                        sendEffect(ChatRoomEffect.ScrollToBottom)
                    }
                    is DataResult.Error -> setState { it.copy(isLoading = false) }
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun sendMessage() {
        val content = uiState.value.currentMessage
        val roomId = currentRoomId ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            setState { it.copy(currentMessage = "") }
            repository.sendMessage(roomId, content)
        }
    }

    private fun handleTyping(isTyping: Boolean) {
        val roomId = currentRoomId ?: return
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            repository.setTypingStatus(roomId, isTyping)
            if (isTyping) {
                delay(3000)
                repository.setTypingStatus(roomId, false)
            }
        }
    }

    private fun handleMessageRead(messageId: String) {
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            repository.markAsRead(roomId, messageId)
        }
    }
}
