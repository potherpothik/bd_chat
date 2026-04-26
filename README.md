# BD Chat

An Android chat application built with Kotlin, Jetpack Compose, and a simple repository-driven architecture.

## How to implement the chat app

This project is organized in a few clear layers:

- **UI layer** (`app/src/main/java/com/bdchat/app/ui/AppShell.kt`)
  - Compose screens for chat list, conversation view, and placeholders.
- **State layer** (`app/src/main/java/com/bdchat/app/viewmodel`)
  - `ChatViewModel` and `ChatUiState` expose app state and actions.
- **Data layer** (`app/src/main/java/com/bdchat/app/data/FakeChatRepository.kt`)
  - In-memory source for contacts, threads, and messages.
- **Domain models** (`app/src/main/java/com/bdchat/app/model/ChatModels.kt`)
  - Message, thread, contact, and status models.

Use the steps below to implement (or extend) the chat app in a production-ready direction.

### 1) Create the data model

Define core models in `ChatModels.kt`:

- `ChatContact`: id, name, phone
- `ChatMessage`: id, threadId, senderId, text, sentAt, status
- `ChatThread`: id, contact, messages, isOnline, plus computed fields:
  - `latestMessage`
  - `unreadCount`

Keep one constant user id (for example `CURRENT_USER_ID = "me"`) to determine outgoing vs incoming messages.

### 2) Implement repository operations

Start with `FakeChatRepository` to keep development fast:

- Seed contacts and initial messages.
- Add thread and message reads:
  - `getThreads()`
  - `getMessages(threadId)`
- Add write actions:
  - `sendMessage(threadId, messageText)`
  - `createConversation()`

When moving to real backend support, replace this repository with a remote source (REST/WebSocket) while keeping the same API for the ViewModel.

### 3) Build state management with ViewModel

In `ChatViewModel`:

- Hold app state in `MutableStateFlow<ChatUiState>`.
- Load initial data in `init`.
- Expose user actions:
  - `openConversation(chatId)`
  - `closeConversation()`
  - `sendMessage(chatId, content)`
  - `createQuickConversation()`

Use `viewModelScope.launch` for async operations and call a single `refreshState()` method after mutations to keep UI updates consistent.

### 4) Build the Compose UI

Implement screens inside `AppShell.kt`:

- `ChatApp(...)`: root composable that switches between list and conversation detail.
- `ChatsScreen(...)`: thread list with preview text and unread badge.
- `ConversationScreen(...)`: message timeline + input composer.
- `MessageBubble(...)`: align and style outgoing vs incoming messages.

UI behavior to include:

- Tap a thread to open conversation detail.
- Use top-bar back action to return to chat list.
- Prevent blank messages from being sent.
- Clear draft text after a successful send.

### 5) Wire app entry point

In `MainActivity.kt`:

- Apply theme (`BDChatTheme`).
- Create and provide `ChatViewModel`.
- Render `ChatApp(viewModel)`.

### 6) Add tests for repository behavior

Use unit tests in `app/src/test/.../FakeChatRepositoryTest.kt`:

- sending appends a new message
- blank messages are rejected
- thread sorting by latest activity works
- unknown thread returns empty list

As you add features, also add tests for new edge cases (long text, duplicate sends, timestamp ordering).

### 7) Run and validate locally

From the repository root:

```bash
./gradlew test
./gradlew assembleDebug
```

Then run on emulator/device from Android Studio to validate:

- thread ordering
- unread badge updates
- send flow and input clearing
- navigation between list and conversation

### 8) Suggested next implementation steps

To evolve this into a full chat product:

1. Replace `FakeChatRepository` with a real data source.
2. Add persistence (Room) for offline-first behavior.
3. Introduce real-time updates via WebSocket or Firebase.
4. Add authentication and per-user thread/message isolation.
5. Track delivery/read receipts from backend events.
6. Add pagination for long conversation histories.
7. Add UI/instrumentation tests for core flows.

---

If you follow the structure above, you can keep UI, state, and data concerns separated and swap fake data for backend integration with minimal UI changes.
