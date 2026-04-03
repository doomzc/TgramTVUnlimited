package com.momoko.tgramtvunlimited.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatScreen() {
    var messageInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Back Button
        Button(onClick = { /* Handle back action */ }) {
            BasicText(text = "Back", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Message List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Message Input Field
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { BasicText("Type a message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (messageInput.isNotBlank()) {
                    messages.add(messageInput)
                    messageInput = ""
                }
            }) {
                BasicText(text = "Send", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MessageBubble(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        backgroundColor = Color.LightGray
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(8.dp),
            fontSize = 18.sp
        )
    }
}