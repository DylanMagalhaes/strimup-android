package com.strimup.core.ui.component.player

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class YouTubePlayerBridge(private val state: YouTubePlayerState) {

    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onStateChange(code: Int) {
        mainHandler.post { state.onPlaybackStateChanged(YouTubePlaybackState.fromCode(code)) }
    }

    @JavascriptInterface
    fun onProgress(currentTime: Double, duration: Double) {
        mainHandler.post { state.onProgress(currentTime.toFloat(), duration.toFloat()) }
    }

    companion object {
        const val NAME = "StrimupPlayer"
    }
}
