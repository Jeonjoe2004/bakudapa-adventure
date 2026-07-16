package com.bakudapa.adventure.feature.chat.ui

import android.net.Uri
import com.bakudapa.adventure.core.base.UiEffect
import com.bakudapa.adventure.core.base.UiEvent
import com.bakudapa.adventure.core.base.UiState
import com.bakudapa.adventure.feature.chat.domain.model.ChatRoom
import com.bakudapa.adventure.feature.chat.domain.model.Message

// Chat List Contract
data class ChatListState(
    val isLoading: Boolean = false,
    val rooms: List<ChatRoom> = emptyList(),
    val error: String? = null
) : UiState

sealed class ChatListEvent : UiEvent {
    object LoadRooms : ChatListEvent()
    data class OnRoomClicked(val roomId: String) : ChatListEvent()
    object OnCreateGroupClicked : ChatListEvent()
}

sealed class ChatListEffect : UiEffect {
    data class NavigateToChatRoom(val roomId: String) : ChatListEffect()
    object NavigateToCreateGroup : ChatListEffect()
}

// Chat Room Contract
data class ChatRoomState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val room: ChatRoom? = null,
    val currentMessage: String = "",
    val error: String? = null,
    val isOtherUserTyping: Boolean = false,
    val otherUserOnline: Boolean = false,
    val isUploadingMedia: Boolean = false
) : UiState

sealed class ChatRoomEvent : UiEvent {
    data class LoadMessages(val roomId: String) : ChatRoomEvent()
    data class OnMessageChanged(val content: String) : ChatRoomEvent()
    object OnSendClicked : ChatRoomEvent()
    data class OnTyping(val isTyping: Boolean) : ChatRoomEvent()
    data class OnMessageRead(val messageId: String) : ChatRoomEvent()
    object OnMediaClicked : ChatRoomEvent()
    data class OnMediaSelected(val uri: Uri, val isImage: Boolean) : ChatRoomEvent()
}

sealed class ChatRoomEffect : UiEffect {
    object ScrollToBottom : ChatRoomEffect()
    data class ShowError(val message: String) : ChatRoomEffect()
    object PickMedia : ChatRoomEffect()
}
