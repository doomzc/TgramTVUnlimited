package com.momoko.tgramtvunlimited.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaPlayerScreen() {
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var progress by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress Bar
        LinearProgressIndicator(progress)

        // Speed Control
        Row {
            TextButton(onClick = { playbackSpeed = 0.5f }) { Text("0.5x") }
            TextButton(onClick = { playbackSpeed = 1.0f }) { Text("1.0x") }
            TextButton(onClick = { playbackSpeed = 1.5f }) { Text("1.5x") }
            TextButton(onClick = { playbackSpeed = 2.0f }) { Text("2.0x") }
        }

        // Play/Pause Button
        Button(onClick = { isPlaying = !isPlaying }) {
            Text(if (isPlaying) "Pause" else "Play")
        }

        // Back Button
        Button(onClick = { /* Handle back action */ }) {
            Text("Back")
        }
    }
}