package com.momoko.tgramtvunlimited.data.api

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi

class TelegramApiClient(private val context: Context) {
    
    companion object {
        private const val TAG = "TelegramApiClient"
        private const val API_ID = 38369355
        private const val API_HASH = "258de4cf5d43fff96cb75454527b2e16"
        
        @Volatile
        private var instance: TelegramApiClient? = null
        
        fun getInstance(context: Context): TelegramApiClient {
            return instance ?: synchronized(this) {
                instance ?: TelegramApiClient(context).also { instance = it }
            }
        }
    }

    private var client: Client? = null
    
    private val _authState = MutableStateFlow<String>("Initializing")
    val authState: StateFlow<String> = _authState
    
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    fun initialize() {
        Client.setLogMessageHandler(0) { level, message -> 
            Log.d(TAG, message) 
        }
        
        client = Client.create({ update -> handleUpdate(update) }, 
                               { update -> handleUpdate(update) }, 
                               { e -> Log.e(TAG, "Error", e) })
        
        _authState.value = "Initialized"
    }

    private fun handleUpdate(update: TdApi.Object) {
        when (update) {
            is TdApi.UpdateAuthorizationState -> {
                _authState.value = update.authorizationState.toString()
            }
        }
    }

    fun setPhoneNumber(phoneNumber: String) {
        client?.send(TdApi.SetAuthenticationPhoneNumber(
            phoneNumber, 
            TdApi.PhoneNumberAuthenticationSettings()
        )) { }
    }

    fun checkCode(code: String) {
        client?.send(TdApi.CheckAuthenticationCode(code)) { }
    }

    fun close() {
        client?.send(TdApi.Close()) { }
    }
}

data class Chat(
    val id: Long,
    val title: String,
    val type: String
)

data class Message(
    val id: Long,
    val chatId: Long,
    val text: String
)
