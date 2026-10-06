package com.strimup.core.ui.component.player

import android.webkit.WebView

class WebViewYouTubePlayerCommands(private val webView: WebView) : YouTubePlayerCommands {

    override fun play() = evaluate("playVideo()")

    override fun pause() = evaluate("pauseVideo()")

    override fun seekTo(seconds: Float) = evaluate("seekVideo($seconds)")

    private fun evaluate(script: String) {
        webView.evaluateJavascript(script, null)
    }
}
