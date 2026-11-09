package com.strimup.core.ui.component.twitch

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil3.compose.AsyncImage
import com.strimup.core.streamer.domain.entity.EMBED_PARENT_DOMAIN
import com.strimup.core.ui.component.embed.configureForEmbed
import com.strimup.core.ui.component.embed.loadEmbedHtml
import kotlinx.coroutines.delay

private const val READY_POLL_INTERVAL_MS = 400L
private const val PLACEHOLDER_FADE_MS = 400

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
    var hasStartedPlaying by remember(channel) { mutableStateOf(false) }
    val placeholderAlpha by animateFloatAsState(
        targetValue = if (hasStartedPlaying) 0f else 1f,
        animationSpec = tween(PLACEHOLDER_FADE_MS),
        label = "twitch_placeholder_alpha",
    )

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    LaunchedEffect(webView, isPlaying) {
        webView?.evaluateJavascript("window.strimupSetPlaying($isPlaying);", null)
    }

    LaunchedEffect(webView, isMuted) {
        webView?.evaluateJavascript("window.strimupSetMuted($isMuted);", null)
    }

    LaunchedEffect(webView, channel) {
        val view = webView ?: return@LaunchedEffect
        while (!hasStartedPlaying) {
            delay(READY_POLL_INTERVAL_MS)
            view.evaluateJavascript("window.strimupHasPlayed === true") { result ->
                if (result == "true") hasStartedPlaying = true
            }
        }
    }

    Box(modifier = modifier.background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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

        if (placeholderAlpha > 0f) {
            AsyncImage(
                model = twitchLivePreviewUrl(channel),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(placeholderAlpha),
            )
        }
    }
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
    <script>
    var wantPlaying = $startPlaying;
    var wantMuted = $startMuted;
    var player = null;
    var isReady = false;
    window.strimupHasPlayed = false;
    function applyState() {
        if (!player || !isReady) { return; }
        player.setMuted(wantMuted);
        if (wantPlaying) { player.play(); } else { player.pause(); }
    }
    window.strimupSetMuted = function(muted) { wantMuted = muted; applyState(); };
    window.strimupSetPlaying = function(playing) { wantPlaying = playing; applyState(); };
    </script>
    <script src="https://player.twitch.tv/js/embed/v1.js"></script>
    <script>
    player = new Twitch.Player("player", {
        channel: "$channel",
        parent: ["$EMBED_PARENT_DOMAIN"],
        width: "100%",
        height: "100%",
        muted: $startMuted,
        autoplay: $startPlaying,
        controls: false
    });
    player.addEventListener(Twitch.Player.READY, function() { isReady = true; applyState(); });
    player.addEventListener(Twitch.Player.PLAYING, function() { window.strimupHasPlayed = true; });
    player.addEventListener(Twitch.Player.PAUSE, function() { if (wantPlaying) { player.play(); } });
    </script>
    </body>
    </html>
""".trimIndent()
