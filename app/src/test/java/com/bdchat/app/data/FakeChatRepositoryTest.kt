package com.bdchat.app.data

import com.bdchat.app.model.ChatMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeChatRepositoryTest {

    @Test
    fun sendMessage_appendsNewMessage() = runBlocking {
        val repository = FakeChatRepository()
        val initialMessages = repository.messagesFor("thread_1")
        val originalSize = initialMessages.size

        val sent = repository.sendMessage("thread_1", "Hello from test")

        val afterMessages = repository.messagesFor("thread_1")
        assertEquals(originalSize + 1, afterMessages.size)
        assertEquals(sent, afterMessages.last())
        assertEquals("Hello from test", sent?.text)
    }

    @Test
    fun sendMessage_doesNotCreateWhenBlank() = runBlocking {
        val repository = FakeChatRepository()
        val beforeSize = repository.messagesFor("thread_2").size

        val sent = repository.sendMessage("thread_2", "   ")

        val afterSize = repository.messagesFor("thread_2").size
        assertEquals(beforeSize, afterSize)
        assertEquals(null, sent)
    }

    @Test
    fun chatSummaries_returnInDescendingLastUpdatedOrder() = runBlocking {
        val repository = FakeChatRepository()

        repository.sendMessage("thread_2", "Newest")
        val summaries = repository.chatSummaries()

        assertTrue(summaries.isNotEmpty())
        assertEquals("thread_2", summaries.first().id)
    }

    @Test
    fun messagesFor_unknownChat_returnsEmptyList() = runBlocking {
        val repository = FakeChatRepository()
        val messages: List<ChatMessage> = repository.messagesFor("missing")
        assertFalse(messages.isNotEmpty())
    }
}
