package com.bdchat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bdchat.app.ui.ChatApp
import com.bdchat.app.ui.theme.BDChatTheme
import com.bdchat.app.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BDChatTheme {
                val chatViewModel: ChatViewModel = viewModel()
                ChatApp(viewModel = chatViewModel)
            }
        }
    }
}

