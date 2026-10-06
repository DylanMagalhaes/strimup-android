package com.strimup.core.ui.component.player

enum class YouTubePlaybackState(val code: Int, val isPlaying: Boolean) {
    UNSTARTED(code = -1, isPlaying = false),
    ENDED(code = 0, isPlaying = false),
    PLAYING(code = 1, isPlaying = true),
    PAUSED(code = 2, isPlaying = false),
    BUFFERING(code = 3, isPlaying = true),
    CUED(code = 5, isPlaying = false),
    ;

    companion object {
        fun fromCode(code: Int): YouTubePlaybackState =
            entries.firstOrNull { it.code == code } ?: UNSTARTED
    }
}
