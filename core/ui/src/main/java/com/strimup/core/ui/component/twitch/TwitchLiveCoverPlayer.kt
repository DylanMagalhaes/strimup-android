package com.strimup.core.ui.component.twitch

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
import com.strimup.core.streamer.domain.entity.EMBED_PARENT_DOMAIN
import com.strimup.core.ui.component.embed.configureForEmbed
import com.strimup.core.ui.component.embed.loadEmbedHtml

fun twitchLivePreviewUrl(channel: String, width: Int = 640, height: Int = 360): String =
    "https://static-cdn.jtvnw.net/previews-ttv/live_user_$channel-${width}x$height.jpg"

@Composable
fun TwitchLiveCoverPlayer(
    channel: String,
    isPlaying: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    LaunchedEffect(webView, isPlaying) {
        webView?.evaluateJavascript("window.strimupSetPlaying && window.strimupSetPlaying($isPlaying);", null)
    }

    LaunchedEffect(webView, isMuted) {
        webView?.evaluateJavascript("window.strimupSetMuted && window.strimupSetMuted($isMuted);", null)
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
            if (view.tag != channel) {
                view.tag = channel
                view.loadEmbedHtml(twitchCoverHtml(channel, startMuted = isMuted, startPlaying = isPlaying))
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

private fun twitchCoverHtml(channel: String, startMuted: Boolean, startPlaying: Boolean): String = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <style>
    html, body { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
    #player {
        position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
        width: max(100vw, 177.78vh); height: max(100vh, 56.25vw);
        pointer-events: none;
    }
    #player iframe { border: 0; width: 100%; height: 100%; }
    </style>
    </head>
    <body>
    <div id="player"></div>
    <script src="https://player.twitch.tv/js/embed/v1.js"></script>
    <script>
    var player = new Twitch.Player("player", {
        channel: "$channel",
        parent: ["$EMBED_PARENT_DOMAIN"],
        width: "100%",
        height: "100%",
        muted: $startMuted,
        autoplay: $startPlaying,
        controls: false
    });
    window.strimupSetMuted = function(muted) { player.setMuted(muted); };
    window.strimupSetPlaying = function(playing) { if (playing) { player.play(); } else { player.pause(); } };
    </script>
    </body>
    </html>
""".trimIndent()
