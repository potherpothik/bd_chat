package com.bdchat.app.data

import com.bdchat.app.model.ChatContact
import com.bdchat.app.model.ChatMessage
import com.bdchat.app.model.ChatThread
import com.bdchat.app.model.MessageStatus
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class FakeChatRepository {

    private val contacts = mutableListOf(
        ChatContact(id = "c1", name = "Ayesha Khan", phone = "+8801711122334"),
        ChatContact(id = "c2", name = "Rahim Ahmed", phone = "+8801813344556"),
        ChatContact(id = "c3", name = "Tasnima Islam", phone = "+8801912233445"),
        ChatContact(id = "c4", name = "Team BD Chat", phone = "group")
    )

    private val messagesByThread: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf(
        "c1" to mutableListOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                threadId = "c1",
                senderId = "c1",
                text = "Hey, are you joining dinner tonight?",
                sentAt = Instant.now().minus(45, ChronoUnit.MINUTES),
                status = MessageStatus.READ
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                threadId = "c1",
                senderId = "me",
                text = "Yes, I will be there by 9.",
                sentAt = Instant.now().minus(41, ChronoUnit.MINUTES),
                status = MessageStatus.READ
            )
        ),
        "c2" to mutableListOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                threadId = "c2",
                senderId = "c2",
                text = "Did you check the deployment logs?",
                sentAt = Instant.now().minus(2, ChronoUnit.HOURS),
                status = MessageStatus.DELIVERED
            )
        ),
        "c3" to mutableListOf(),
        "c4" to mutableListOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                threadId = "c4",
                senderId = "c4",
                text = "Reminder: standup at 10 AM tomorrow.",
                sentAt = Instant.now().minus(7, ChronoUnit.HOURS),
                status = MessageStatus.DELIVERED
            )
        )
    )

    fun getThreads(): List<ChatThread> {
        return contacts.map { contact ->
            val messages = messagesByThread[contact.id].orEmpty().sortedBy { it.sentAt }
            ChatThread(
                id = contact.id,
                contact = contact,
                messages = messages,
                isOnline = contact.id != "c2"
            )
        }.sortedByDescending { thread -> thread.latestMessage?.sentAt ?: Instant.EPOCH }
    }

    fun getMessages(threadId: String): List<ChatMessage> {
        return messagesByThread[threadId].orEmpty().sortedBy { it.sentAt }
    }

    fun sendMessage(threadId: String, messageText: String): ChatMessage? {
        if (messageText.isBlank()) return null
        val threadMessages = messagesByThread.getOrPut(threadId) { mutableListOf() }
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            threadId = threadId,
            senderId = "me",
            text = messageText.trim(),
            sentAt = Instant.now(),
            status = MessageStatus.SENT
        )
        threadMessages.add(message)
        return message
    }

    fun createConversation(): String {
        val nextIndex = contacts.size + 1
        val id = "n$nextIndex"
        val contact = ChatContact(
            id = id,
            name = "New Contact $nextIndex",
            phone = "+880170000$nextIndex"
        )
        contacts.add(0, contact)
        messagesByThread[id] = mutableListOf(
            ChatMessage(
                threadId = id,
                senderId = id,
                text = "Hi! This is a new conversation.",
                status = MessageStatus.DELIVERED
            )
        )
        return id
    }
}
