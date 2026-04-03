package com.momoko.tgramtvunlimited.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

sealed class NavScreen {  
    object ChatList : NavScreen()  
    object Chat : NavScreen()  
    object MediaPlayer : NavScreen()  
}

@Composable
fun AppNavigation() {  
    var currentScreen by remember { mutableStateOf<NavScreen>(NavScreen.ChatList) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {  
        when (currentScreen) {  
            is NavScreen.ChatList -> {  
                Text("Chat List Screen")  
                Button(onClick = { currentScreen = NavScreen.Chat }) {  
                    Text("Go to Chat")  
                }  
                Button(onClick = { currentScreen = NavScreen.MediaPlayer }) {  
                    Text("Go to Media Player")  
                }  
            }  
            is NavScreen.Chat -> {  
                Text("Chat Screen")  
                Button(onClick = { currentScreen = NavScreen.ChatList }) {  
                    Text("Back to Chat List")  
                }  
            }  
            is NavScreen.MediaPlayer -> {  
                Text("Media Player Screen")  
                Button(onClick = { currentScreen = NavScreen.ChatList }) {  
                    Text("Back to Chat List")  
                }  
            }  
        }  
    }  
}  
