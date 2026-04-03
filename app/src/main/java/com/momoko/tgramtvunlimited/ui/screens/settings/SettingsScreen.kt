package com.momoko.tgramtvunlimited.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.momoko.tgramtvunlimited.data.api.TelegramApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val api = remember { TelegramApi.getInstance(context) }
    
    var autoPlayVideos by remember { mutableStateOf(true) }
    var downloadOverWifi by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var autoPlayVoice by remember { mutableStateOf(true) }
    var showNotifications by remember { mutableStateOf(true) }
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 播放设置
            Text(
                text = "播放设置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "自动播放视频",
                subtitle = "进入聊天时自动播放视频",
                checked = autoPlayVideos,
                onCheckedChange = { autoPlayVideos = it }
            )
            
            SettingsItem(
                title = "自动播放语音",
                subtitle = "自动播放语音消息",
                checked = autoPlayVoice,
                onCheckedChange = { autoPlayVoice = it }
            )
            
            SettingsItem(
                title = "无限播放",
                subtitle = "视频和语音可完整播放，无时长限制",
                checked = true,
                onCheckedChange = {},
                enabled = false
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // 下载设置
            Text(
                text = "下载设置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "仅 Wi-Fi 下载",
                subtitle = "移动网络不自动下载文件",
                checked = downloadOverWifi,
                onCheckedChange = { downloadOverWifi = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // 界面设置
            Text(
                text = "界面设置",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "深色模式",
                subtitle = "使用深色主题",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
            
            SettingsItem(
                title = "通知",
                subtitle = "接收新消息通知",
                checked = showNotifications,
                onCheckedChange = { showNotifications = it }
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // 关于
            Text(
                text = "关于",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "版本",
                subtitle = "Tgram TV Unlimited v1.0.0"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 退出登录按钮
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("退出登录")
            }
        }
    }
    
    // 退出登录确认对话框
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出当前账号吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // api.logout()
                        showLogoutDialog = false
                        onBack()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String = "",
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && onCheckedChange != null) {
                checked?.let { onCheckedChange?.invoke(!it) }
            }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        if (checked != null && onCheckedChange != null) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
}