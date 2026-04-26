package com.bdchat.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bdchat.app.model.ChatMessage
import com.bdchat.app.model.ChatThread
import com.bdchat.app.viewmodel.ChatViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatApp(viewModel: ChatViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Chats) }
    val selectedThreadId = uiState.selectedThreadId

    if (selectedThreadId != null) {
        val thread = uiState.threads.firstOrNull { it.id == selectedThreadId }
        if (thread != null) {
            ConversationScreen(
                thread = thread,
                onSend = { content -> viewModel.sendMessage(thread.id, content) },
                onBack = viewModel::closeConversation
            )
        } else {
            viewModel.closeConversation()
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == BottomTab.Chats) {
                FloatingActionButton(onClick = viewModel::createQuickConversation) {
                    Icon(Icons.Default.Add, contentDescription = "New chat")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(tab.icon) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            BottomTab.Chats -> ChatsScreen(
                threads = uiState.threads,
                contentPadding = padding,
                onThreadSelected = viewModel::openConversation
            )
            BottomTab.Updates -> PlaceholderTab(
                title = "Updates",
                subtitle = "Share quick status moments with your contacts.",
                contentPadding = padding
            )
            BottomTab.Calls -> PlaceholderTab(
                title = "Calls",
                subtitle = "Recent voice and video calls appear here.",
                contentPadding = padding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsScreen(
    threads: List<ChatThread>,
    contentPadding: PaddingValues,
    onThreadSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "BD Chat",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        )
        Divider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(threads, key = { it.id }) { thread ->
                ConversationRow(
                    thread = thread,
                    onClick = { onThreadSelected(thread.id) }
                )
                Divider(modifier = Modifier.padding(start = 76.dp))
            }
        }
    }
}

@Composable
private fun ConversationRow(
    thread: ChatThread,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = thread.contact.name.take(1),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = thread.contact.name, fontWeight = FontWeight.SemiBold)
            Text(
                text = thread.latestMessage?.text ?: "No messages yet",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = thread.latestMessage?.sentAt?.atZone(ZoneId.systemDefault())?.format(FORMATTER) ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (thread.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(22.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = thread.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationScreen(
    thread: ChatThread,
    onSend: (String) -> Unit,
    onBack: () -> Unit
) {
    var draft by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(thread.contact.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(thread.messages.reversed(), key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Type a message") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSend(draft.trim())
                            draft = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isMine = message.isOutgoing
    val horizontalAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMine) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isMine) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = message.text, color = textColor)
            Text(
                text = message.sentAt.atZone(ZoneId.systemDefault()).format(FORMATTER),
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    subtitle: String,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private enum class BottomTab(val label: String, val icon: String) {
    Chats("Chats", "C"),
    Updates("Updates", "U"),
    Calls("Calls", "P")
}

private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
