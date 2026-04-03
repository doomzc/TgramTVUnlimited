package com.momoko.tgramtvunlimited.ui.screens.media

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.momoko.tgramtvunlimited.player.PlaybackState
import com.momoko.tgramtvunlimited.player.UnlimitedPlayer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerScreen(
    messageId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val player = remember { UnlimitedPlayer.getInstance(context) }
    
    var showControls by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    
    val playbackState by player.playbackState.collectAsState()
    val currentMedia by player.currentMediaItem.collectAsState()
    val isPlaying by player.isPlaying.collectAsState()
    val position by player.position.collectAsState()
    val duration by player.duration.collectAsState()
    
    // 自动隐藏控制栏
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }
    
    // 初始化播放器
    LaunchedEffect(Unit) {
        player.initialize()
        // 这里应该传入实际的媒体 URL
        // player.play(url, messageId, title)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            // 不释放播放器，返回时保持播放状态
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable()
    ) {
        // 视频播放器
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    player = player.getPlayer()
                    useController = false // 使用自定义控制器
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // 自定义控制栏
        if (showControls) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // 顶部栏
                TopAppBar(
                    title = { Text(currentMedia?.title ?: "媒体播放", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.focusable()
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 中间播放控制
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 后退 10 秒
                    IconButton(
                        onClick = { player.seekBackward(10000) },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "后退10秒",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    // 播放/暂停
                    FilledIconButton(
                        onClick = {
                            if (isPlaying) player.pause() else player.play()
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    // 前进 10 秒
                    IconButton(
                        onClick = { player.seekForward(10000) },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "前进10秒",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 底部进度条
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 进度条
                    Slider(
                        value = if (duration > 0) position.toFloat() / duration.toFloat() else 0f,
                        onValueChange = { value ->
                            player.seekTo((value * duration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // 时间显示
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatDuration(position),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatDuration(duration),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 播放速度控制
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("速度:", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            TextButton(
                                onClick = { player.setPlaybackSpeed(speed) }
                            ) {
                                Text(
                                    text = "${speed}x",
                                    color = if (speed == 1.0f) MaterialTheme.colorScheme.primary else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // 点击切换控制栏显示
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .focusable()
        ) {
            // 空覆盖层，用于点击显示/隐藏控制栏
        }
        
        // 加载状态
        if (playbackState is PlaybackState.Buffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}