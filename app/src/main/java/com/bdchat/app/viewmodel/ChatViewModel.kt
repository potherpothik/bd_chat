package com.bdchat.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdchat.app.data.FakeChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: FakeChatRepository = FakeChatRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        refreshState()
    }

    fun sendMessage(chatId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(chatId, content)
            refreshState()
        }
    }

    fun createQuickConversation() {
        viewModelScope.launch {
            repository.createConversation()
            refreshState()
        }
    }

    fun openConversation(chatId: String) {
        _uiState.update { current ->
            current.copy(selectedThreadId = chatId)
        }
    }

    fun closeConversation() {
        _uiState.update { current ->
            current.copy(selectedThreadId = null)
        }
    }

    private fun refreshState() {
        _uiState.update { current ->
            ChatUiState(
                threads = repository.getThreads(),
                selectedThreadId = current.selectedThreadId
            )
        }
    }
}
