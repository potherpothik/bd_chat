package com.bdchat.app.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

data class ChatContact(
    val id: String,
    val name: String,
    val phone: String
)

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val threadId: String,
    val senderId: String,
    val text: String,
    val sentAt: Instant = Instant.now(),
    val status: MessageStatus = MessageStatus.SENT
) {
    val isOutgoing: Boolean
        get() = senderId == CURRENT_USER_ID
}

data class ChatThread(
    val id: String,
    val contact: ChatContact,
    val messages: List<ChatMessage>,
    val isOnline: Boolean
) {
    val latestMessage: ChatMessage?
        get() = messages.maxByOrNull { it.sentAt }

    val unreadCount: Int
        get() = messages.count { !it.isOutgoing }
}

fun ChatThread.previewText(): String = latestMessage?.text ?: "No messages yet"

const val CURRENT_USER_ID = "me"
