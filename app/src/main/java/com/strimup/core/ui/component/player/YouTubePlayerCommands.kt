package com.strimup.core.ui.component.player

interface YouTubePlayerCommands {
    fun play()
    fun pause()
    fun seekTo(seconds: Float)
}
