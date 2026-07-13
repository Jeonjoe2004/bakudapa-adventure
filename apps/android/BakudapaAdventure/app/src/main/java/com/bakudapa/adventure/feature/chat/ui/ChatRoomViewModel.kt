package com.bakudapa.adventure.feature.chat.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.chat.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: ChatRepository
) : BaseViewModel<ChatRoomState, ChatRoomEvent, ChatRoomEffect>(ChatRoomState()) {

    private var currentRoomId: String? = null
    private var typingJob: Job? = null

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
            ChatRoomEvent.OnMediaClicked -> { /* TODO */ }
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
