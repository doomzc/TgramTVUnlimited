package com.momoko.tgramtvunlimited.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.momoko.tgramtvunlimited.data.api.AuthorizationState
import com.momoko.tgramtvunlimited.data.api.TelegramApi

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { TelegramApi.getInstance(context) }
    
    var phoneNumber by remember { mutableStateOf("") }
    var authCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var qrCodeUrl by remember { mutableStateOf<String?>(null) }
    
    var showPhoneInput by remember { mutableStateOf(true) }
    var showCodeInput by remember { mutableStateOf(false) }
    var showPasswordInput by remember { mutableStateOf(false) }
    var showQrCode by remember { mutableStateOf(false) }
    
    val authState by api.authorizationState.collectAsState()
    
    // 监听授权状态变化
    LaunchedEffect(authState) {
        when (authState) {
            is AuthorizationState.WaitingCode -> {
                showPhoneInput = false
                showCodeInput = true
            }
            is AuthorizationState.WaitingPassword -> {
                showCodeInput = false
                showPasswordInput = true
            }
            is AuthorizationState.Ready -> {
                onLoginSuccess()
            }
            else -> {}
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(500.dp)
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Logo 和标题
                Text(
                    text = "📺 Tgram TV",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Android TV Telegram 客户端",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 手机号输入
                if (showPhoneInput) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("手机号 (+86...)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = {
                            // 初始化 Telegram API (已在 api.initialize() 中配置凭据)
                            api.initialize()
                            api.sendPhoneNumber(phoneNumber)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = phoneNumber.isNotBlank()
                    ) {
                        Text("下一步")
                    }
                    
                    TextButton(
                        onClick = { showQrCode = !showQrCode }
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("使用二维码登录")
                    }
                }
                
                // 验证码输入
                if (showCodeInput) {
                    OutlinedTextField(
                        value = authCode,
                        onValueChange = { authCode = it },
                        label = { Text("验证码") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = { api.sendCode(authCode) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authCode.isNotBlank()
                    ) {
                        Text("确认")
                    }
                }
                
                // 密码输入（两步验证）
                if (showPasswordInput) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("两步验证密码") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    
                    Button(
                        onClick = { api.sendPassword(password) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = password.isNotBlank()
                    ) {
                        Text("确认")
                    }
                }
                
                // 二维码登录
                if (showQrCode) {
                    Text(
                        text = "请使用 Telegram 扫描二维码登录",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    // 二维码显示区域（需要集成二维码库）
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("二维码区域", color = Color.White)
                    }
                }
                
                // 错误提示
                Text(
                    text = "注意：需要 Telegram API 授权",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}