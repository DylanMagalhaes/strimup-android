package com.strimup.core.ui.component.embed

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.playerUrl

private const val TIKTOK_HIDDEN_CONTROLS = "&controls=0&progress_bar=0&play_button=0&volume_control=0" +
    "&fullscreen_button=0&timestamp=0&music_info=0&description=0&native_context_menu=0&closed_caption=0"

private const val PLAYER_CONTROL_SCRIPT = """
    var frame = document.getElementById("player");
    var retryTimer = null;
    function post(message) {
        if (frame && frame.contentWindow) { frame.contentWindow.postMessage(message, "*"); }
    }
    function youtubeCommand(name) {
        post(JSON.stringify({ event: "command", func: name, args: [] }));
    }
    function applyState() {
        if (provider === "YOUTUBE") {
            post(JSON.stringify({ event: "listening", id: 1 }));
            youtubeCommand(wantMuted || !wantPlaying ? "mute" : "unMute");
            youtubeCommand(wantPlaying ? "playVideo" : "pauseVideo");
        }
    }
    function applyStateForAWhile() {
        if (retryTimer) { clearInterval(retryTimer); }
        var attempts = 0;
        applyState();
        retryTimer = setInterval(function() {
            applyState();
            attempts++;
            if (attempts >= 12) { clearInterval(retryTimer); retryTimer = null; }
        }, 400);
    }
    window.strimupSet = function(playing, muted) {
        wantPlaying = playing;
        wantMuted = muted;
        applyStateForAWhile();
    };
    frame.addEventListener("load", applyStateForAWhile);
"""

val VideoEmbed.supportsPlaybackControl: Boolean
    get() = provider == VideoEmbed.Provider.YOUTUBE

@Composable
fun InlineEmbedPlayer(
    embed: VideoEmbed,
    isPlaying: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    LaunchedEffect(webView, isPlaying, isMuted) {
        webView?.evaluateJavascript("window.strimupSet && window.strimupSet($isPlaying, $isMuted);", null)
    }

    AndroidView(
        modifier = modifier.background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                configureForEmbed()
                webView = this
            }
        },
        update = { view ->
            if (view.tag == null) {
                val url = embed.inlinePlayerUrl(startPlaying = isPlaying, startMuted = isMuted)
                view.tag = url
                view.loadEmbedHtml(inlinePlayerHtml(url, embed.provider, isPlaying, isMuted))
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

private fun VideoEmbed.inlinePlayerUrl(startPlaying: Boolean, startMuted: Boolean): String {
    val baseUrl = playerUrl(autoplay = true)
    val muteFlag = if (startMuted || !startPlaying) 1 else 0
    return when (provider) {
        VideoEmbed.Provider.YOUTUBE ->
            "$baseUrl&mute=$muteFlag&loop=1&playlist=$id&enablejsapi=1" +
                "&controls=0&modestbranding=1&iv_load_policy=3&fs=0&disablekb=1"
        VideoEmbed.Provider.TIKTOK -> "$baseUrl&loop=1$TIKTOK_HIDDEN_CONTROLS"
        VideoEmbed.Provider.TWITCH -> baseUrl
    }
}

private fun inlinePlayerHtml(
    url: String,
    provider: VideoEmbed.Provider,
    startPlaying: Boolean,
    startMuted: Boolean,
): String = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <style>
    html, body { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
    iframe { display: block; border: 0; width: 100%; height: 100%; }
    </style>
    </head>
    <body>
    <iframe
        id="player"
        src="${url.escapeHtmlAttribute()}"
        allow="autoplay; encrypted-media; fullscreen; picture-in-picture"
        referrerpolicy="strict-origin-when-cross-origin"
        allowfullscreen>
    </iframe>
    <script>
    var provider = "${provider.name}";
    var wantPlaying = $startPlaying;
    var wantMuted = $startMuted;
    $PLAYER_CONTROL_SCRIPT
    </script>
    </body>
    </html>
""".trimIndent()
