package com.momoko.tgramtvunlimited.data.api

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.drinkless.tdlib.TdApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Telegram TDLib API 封装
 * 处理登录、聊天、消息收发等核心功能
 */
class TelegramApi(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: TelegramApi? = null
        
        fun getInstance(context: Context): TelegramApi {
            return instance ?: synchronized(this) {
                instance ?: TelegramApi(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private var client: TdApi.Client? = null
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _authorizationState = MutableStateFlow<AuthorizationState>(AuthorizationState.None)
    val authorizationState: StateFlow<AuthorizationState> = _authorizationState
    
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats
    
    private var currentUser: User? = null
    private val messageHandlers = mutableMapOf<Long, suspend (Message) -> Unit>()
    private val updateHandlers = mutableListOf<suspend (TdApi.Update) -> Unit>()
    
    // 初始化 TDLib
    // API 凭据：API ID: 38369355, API Hash: 258de4cf5d43fff96cb75454527b2e16
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            client = TdApi.Client { update ->
                handleUpdate(update)
            }
            
            // 设置 TDLib 参数
            val parameters = TdApi.TdlibParameters()
            parameters.apiId = 38369355
            parameters.apiHash = "258de4cf5d43fff96cb75454527b2e16"
            parameters.deviceModel = "Android TV"
            parameters.applicationVersion = "1.0.0"
            parameters.useMessageDatabase = true
            parameters.useSecretChats = true
            parameters.useTestDc = false
            parameters.databaseEncryptionKey = "0123456789abcdef".toByteArray()
            
            client?.send(TdApi.SetTdlibParameters(parameters)) { result ->
                when (result) {
                    is TdApi.Ok -> {
                        // 参数设置成功，继续
                        client?.send(TdApi.GetAuthorizationState()) { }
                    }
                    is TdApi.Error -> {
                        println("Error setting parameters: ${result.message}")
                    }
                }
            }
        }
    }
    
    private fun handleUpdate(update: TdApi.Update) {
        when (update) {
            is TdApi.UpdateAuthorizationState -> {
                when (update.authorizationState) {
                    is TdApi.AuthorizationStateWaitTdlibParameters -> {
                        // 需要设置Tdlib参数
                    }
                    is TdApi.AuthorizationStateWaitEncryptionKey -> {
                        // 需要加密密钥
                    }
                    is TdApi.AuthorizationStateWaitPhoneNumber -> {
                        _authorizationState.value = AuthorizationState.WaitingPhoneNumber
                    }
                    is TdApi.AuthorizationStateWaitCode -> {
                        _authorizationState.value = AuthorizationState.WaitingCode
                    }
                    is TdApi.AuthorizationStateWaitPassword -> {
                        _authorizationState.value = AuthorizationState.WaitingPassword
                    }
                    is TdApi.AuthorizationStateReady -> {
                        _authorizationState.value = AuthorizationState.Ready
                        loadChats()
                    }
                    else -> {}
                }
            }
            is TdApi.UpdateNewMessage -> {
                val message = update.message
                messageHandlers[message.chatId]?.invoke(message)
            }
            is TdApi.UpdateChatList -> {
                loadChats()
            }
        }
        
        updateHandlers.forEach { it(update) }
    }
    
    // 发送手机号登录
    suspend fun sendPhoneNumber(phoneNumber: String) {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, false, false)) { }
        }
    }
    
    // 发送验证码
    suspend fun sendCode(code: String) {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.CheckAuthenticationCode(code)) { }
        }
    }
    
    // 发送密码
    suspend fun sendPassword(password: String) {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.CheckAuthenticationPassword(password)) { }
        }
    }
    
    // 二维码登录 - 获取 QR 码
    suspend fun getQRCode(): String? {
        return withContext(Dispatchers.IO) {
            var qrUrl: String? = null
            client?.send(TdApi.RequestQrCodeAuthentication()) { result ->
                when (result) {
                    is TdApi.QrCodeAuthenticationLink -> {
                        qrUrl = result.link
                    }
                }
            }
            qrUrl
        }
    }
    
    // 加载聊天列表
    suspend fun loadChats() {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.GetChats(null, 0, 100)) { result ->
                when (result) {
                    is TdApi.Chats -> {
                        val chatList = result.chatIds.map { chatId ->
                            client?.send(TdApi.GetChat(chatId)) { chatResult ->
                                when (chatResult) {
                                    is TdApi.Chat -> {
                                        val chat = Chat(
                                            id = chatResult.id,
                                            title = chatResult.title,
                                            lastMessage = "",
                                            unreadCount = chatResult.unreadCount,
                                            photoUrl = null
                                        )
                                        _chats.value = _chats.value + chat
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 获取聊天详情
    suspend fun getChat(chatId: Long): Chat? {
        return withContext(Dispatchers.IO) {
            var chat: Chat? = null
            client?.send(TdApi.GetChat(chatId)) { result ->
                when (result) {
                    is TdApi.Chat -> {
                        chat = Chat(
                            id = result.id,
                            title = result.title,
                            lastMessage = "",
                            unreadCount = result.unreadCount,
                            photoUrl = null
                        )
                    }
                }
            }
            chat
        }
    }
    
    // 获取聊天历史消息
    suspend fun getChatHistory(chatId: Long, limit: Int = 50): List<Message> {
        return withContext(Dispatchers.IO) {
            val messages = mutableListOf<Message>()
            client?.send(TdApi.GetChatHistory(chatId, 0, 0, limit, false)) { result ->
                when (result) {
                    is TdApi.Messages -> {
                        result.messages.forEach { tdMsg ->
                            messages.add(tdMsg.toMessage())
                        }
                    }
                }
            }
            messages
        }
    }
    
    // 发送消息
    suspend fun sendMessage(chatId: Long, text: String) {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.SendMessage(chatId, 0, 0, null, 
                TdApi.InputMessageText(TdApi.FormattedText(text, emptyList()), false, false)
            )) { }
        }
    }
    
    // 获取文件下载链接
    suspend fun getFileUrl(fileId: Long): String? {
        return withContext(Dispatchers.IO) {
            var url: String? = null
            client?.send(TdApi.GetFileUrl(fileId)) { result ->
                when (result) {
                    is TdApi.FileUrl -> url = result.url
                }
            }
            url
        }
    }
    
    // 注册消息处理器
    fun onMessage(chatId: Long, handler: suspend (Message) -> Unit) {
        messageHandlers[chatId] = handler
    }
    
    // 注册更新处理器
    fun onUpdate(handler: suspend (TdApi.Update) -> Unit) {
        updateHandlers.add(handler)
    }
    
    // 退出登录
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            client?.send(TdApi.LogOut()) { }
            _authorizationState.value = AuthorizationState.None
            _chats.value = emptyList()
        }
    }
    
    // 关闭客户端
    fun close() {
        client?.close()
        client = null
    }
}

// 扩展函数：TdApi.Message 转换为 Message
fun TdApi.Message.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderUserId,
        content = when (content) {
            is TdApi.MessageText -> (content as TdApi.MessageText).text.text
            is TdApi.MessagePhoto -> "📷 相片"
            is TdApi.MessageVideo -> "🎬 影片"
            is TdApi.MessageVoiceNote -> "🎤 语音"
            is TdApi.MessageAudio -> "🎵 音频"
            is TdApi.MessageDocument -> "📎 文件"
            else -> "未知消息"
        },
        date = date,
        isOutgoing = isOutgoing,
        mediaFileId = when (val c = content) {
            is TdApi.MessageVideo -> c.video.video.id
            is TdApi.MessageVoiceNote -> c.voiceNote.voice.id
            is TdApi.MessageAudio -> c.audio.audio.id
            else -> 0
        },
        canPlay = content is TdApi.MessageVideo || 
                  content is TdApi.MessageVoiceNote || 
                  content is TdApi.MessageAudio
    )
}

// 数据类
data class Chat(
    val id: Long,
    val title: String,
    val lastMessage: String,
    val unreadCount: Int,
    val photoUrl: String?
)

data class Message(
    val id: Long,
    val chatId: Long,
    val senderId: Long,
    val content: String,
    val date: Int,
    val isOutgoing: Boolean,
    val mediaFileId: Long = 0,
    val canPlay: Boolean = false
)

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val phoneNumber: String
)

// 授权状态
sealed class AuthorizationState {
    object None : AuthorizationState()
    object WaitingPhoneNumber : AuthorizationState()
    object WaitingCode : AuthorizationState()
    object WaitingPassword : AuthorizationState()
    object Ready : AuthorizationState()
}