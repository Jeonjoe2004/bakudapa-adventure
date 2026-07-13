package com.bakudapa.adventure.feature.chat.ui

import androidx.lifecycle.viewModelScope
import com.bakudapa.adventure.core.base.BaseViewModel
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.chat.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository
) : BaseViewModel<ChatListState, ChatListEvent, ChatListEffect>(ChatListState()) {

    init {
        loadRooms()
    }

    override fun onEvent(event: ChatListEvent) {
        when (event) {
            ChatListEvent.LoadRooms -> loadRooms()
            is ChatListEvent.OnRoomClicked -> sendEffect(ChatListEffect.NavigateToChatRoom(event.roomId))
            ChatListEvent.OnCreateGroupClicked -> sendEffect(ChatListEffect.NavigateToCreateGroup)
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            repository.getChatRooms().collect { result ->
                when (result) {
                    is DataResult.Success -> setState { it.copy(rooms = result.data, isLoading = false) }
                    is DataResult.Error -> setState { it.copy(error = result.exception.message, isLoading = false) }
                    is DataResult.Loading -> setState { it.copy(isLoading = true) }
                }
            }
        }
    }
}
