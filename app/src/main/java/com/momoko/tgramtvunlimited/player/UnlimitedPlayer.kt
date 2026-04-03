package com.momoko.tgramtvunlimited.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ExoPlayer 无限播放模块
 * 核心特点：支持无限制播放任何时长的视频和音频
 */
class UnlimitedPlayer(private val context: Context) {
    
    companion object {
        @Volatile
        private var instance: UnlimitedPlayer? = null
        
        fun getInstance(context: Context): UnlimitedPlayer {
            return instance ?: synchronized(this) {
                instance ?: UnlimitedPlayer(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private var player: ExoPlayer? = null
    
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private val _currentMediaItem = MutableStateFlow<MediaItemInfo?>(null)
    val currentMediaItem: StateFlow<MediaItemInfo?> = _currentMediaItem.asStateFlow()
    
    private val _position = MutableStateFlow(0L)
    val position: StateFlow<Long> = _position.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private var currentFileId: Long = 0
    
    // 初始化 Player
    @OptIn(UnstableApi::class)
    fun initialize(): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context)
                .setMediaSourceFactory(DefaultMediaSourceFactory(context))
                .build()
                .apply {
                    addListener(playerListener)
                }
        }
        return player!!
    }
    
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            _playbackState.value = when (state) {
                Player.STATE_IDLE -> PlaybackState.Idle
                Player.STATE_BUFFERING -> PlaybackState.Buffering
                Player.STATE_READY -> {
                    _duration.value = player?.duration ?: 0L
                    PlaybackState.Ready
                }
                Player.STATE_ENDED -> {
                    // 无限播放：播放完成后重新开始
                    player?.seekTo(0)
                    player?.play()
                    PlaybackState.Playing
                }
                else -> PlaybackState.Idle
            }
        }
        
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) {
                _playbackState.value = PlaybackState.Playing
            }
        }
        
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            _position.value = newPosition.positionMs
        }
    }
    
    /**
     * 播放视频/音频
     * @param url 文件 URL（支持 http, https, file）
     * @param fileId Telegram 文件 ID
     * @param title 标题
     */
    fun play(url: String, fileId: Long = 0, title: String = "") {
        currentFileId = fileId
        
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaId(fileId.toString())
            .build()
        
        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
        
        _currentMediaItem.value = MediaItemInfo(
            fileId = fileId,
            url = url,
            title = title
        )
    }
    
    /**
     * 播放本地文件
     */
    fun playFile(filePath: String, title: String = "") {
        play("file://$filePath", title = title)
    }
    
    /**
     * 播放 Telegram 文件（通过 TDLib 获取下载链接）
     */
    fun playTelegramFile(fileUrl: String, fileId: Long, title: String = "") {
        play(fileUrl, fileId, title)
    }
    
    // 播放控制
    fun play() {
        player?.play()
    }
    
    fun pause() {
        player?.pause()
    }
    
    fun stop() {
        player?.stop()
    }
    
    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }
    
    fun seekForward(ms: Long = 10000) {
        player?.let {
            val newPosition = (it.currentPosition + ms).coerceAtMost(it.duration)
            it.seekTo(newPosition)
        }
    }
    
    fun seekBackward(ms: Long = 10000) {
        player?.let {
            val newPosition = (it.currentPosition - ms).coerceAtLeast(0)
            it.seekTo(newPosition)
        }
    }
    
    // 调整播放速度（支持任意速度）
    fun setPlaybackSpeed(speed: Float) {
        player?.setPlaybackSpeed(speed)
    }
    
    // 音量控制
    fun setVolume(volume: Float) {
        player?.volume = volume.coerceIn(0f, 1f)
    }
    
    // 获取当前位置（实时更新）
    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0L
    }
    
    // 获取总时长
    fun getDuration(): Long {
        return player?.duration ?: 0L
    }
    
    // 是否正在播放
    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }
    
    // 释放资源
    fun release() {
        player?.apply {
            removeListener(playerListener)
            release()
        }
        player = null
        _playbackState.value = PlaybackState.Idle
        _currentMediaItem.value = null
    }
    
    // 获取 ExoPlayer 实例（用于自定义控制）
    fun getPlayer(): ExoPlayer? = player
}

// 播放状态
sealed class PlaybackState {
    object Idle : PlaybackState()
    object Buffering : PlaybackState()
    object Ready : PlaybackState()
    object Playing : PlaybackState()
    object Paused : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}

// 媒体信息
data class MediaItemInfo(
    val fileId: Long,
    val url: String,
    val title: String
)