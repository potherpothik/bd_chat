package com.bdchat.app.viewmodel

import com.bdchat.app.model.ChatThread

data class ChatUiState(
    val threads: List<ChatThread> = emptyList(),
    val selectedThreadId: String? = null
) {
    val selectedThread: ChatThread?
        get() = threads.firstOrNull { it.id == selectedThreadId }
}
