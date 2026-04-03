package com.momoko.tgramtvunlimited.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatItem(val id: Int, val name: String)

@Composable
fun ChatListScreen(chats: List<ChatItem>, onChatSelected: (ChatItem) -> Unit) {
    var selectedChat by remember { mutableStateOf<ChatItem?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chats) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (chat == selectedChat),
                        onClick = { 
                            selectedChat = chat
                            onChatSelected(chat)
                        }
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.name,
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f)
                )
                if (chat == selectedChat) {
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        }
    }
}