package com.momoko.tgramtvunlimited.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UnlimitedPlayer(private val context: Context) : Player.Listener {
    
    companion object {
        @Volatile
        private var instance: UnlimitedPlayer? = null
        
        fun getInstance(context: Context): UnlimitedPlayer {
            return instance ?: synchronized(this) {
                instance ?: UnlimitedPlayer(context).also { instance = it }
            }
        }
    }

    private var exoPlayer: ExoPlayer? = null
    
    private val _playbackState = MutableStateFlow(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun initialize() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer?.addListener(this)
        }
    }

    fun playMedia(url: String) {
        initialize()
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun release() {
        exoPlayer?.removeListener(this)
        exoPlayer?.release()
        exoPlayer = null
    }

    // 🎬 核心：无限播放 - 播放完成自动循环
    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_ENDED -> {
                exoPlayer?.seekTo(0)
                exoPlayer?.play()
            }
            Player.STATE_READY -> {
                _playbackState.value = PlaybackState.Ready
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }
}

enum class PlaybackState {
    Idle, Buffering, Ready, Playing, Paused, Ended, Error
}
