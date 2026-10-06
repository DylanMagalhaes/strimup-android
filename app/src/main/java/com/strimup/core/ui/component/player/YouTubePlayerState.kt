package com.strimup.core.ui.component.player

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class YouTubePlayerState(private val commands: YouTubePlayerCommands) {

    var isPlaying by mutableStateOf(false)
        private set

    var currentTime by mutableFloatStateOf(0f)
        private set

    var duration by mutableFloatStateOf(0f)
        private set

    fun togglePlayPause() {
        if (isPlaying) commands.pause() else commands.play()
    }

    fun seekTo(seconds: Float) {
        val target = if (duration > 0f) seconds.coerceIn(0f, duration) else seconds.coerceAtLeast(0f)
        currentTime = target
        commands.seekTo(target)
    }

    fun seekBy(deltaSeconds: Float) = seekTo(currentTime + deltaSeconds)

    fun onPlaybackStateChanged(playbackState: YouTubePlaybackState) {
        isPlaying = playbackState.isPlaying
    }

    fun onProgress(currentTime: Float, duration: Float) {
        this.currentTime = currentTime
        this.duration = duration
    }
}
