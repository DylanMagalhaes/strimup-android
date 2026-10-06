package com.strimup.core.ui.component.player

class FakeYouTubePlayerCommands : YouTubePlayerCommands {
    var playCount = 0
        private set
    var pauseCount = 0
        private set
    val seeks = mutableListOf<Float>()

    override fun play() {
        playCount++
    }

    override fun pause() {
        pauseCount++
    }

    override fun seekTo(seconds: Float) {
        seeks += seconds
    }
}
