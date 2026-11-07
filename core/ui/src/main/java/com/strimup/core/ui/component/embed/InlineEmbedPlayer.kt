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

val VideoEmbed.supportsSoundControl: Boolean
    get() = provider == VideoEmbed.Provider.YOUTUBE

@Composable
fun InlineEmbedPlayer(
    embed: VideoEmbed,
    isPlaying: Boolean,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val url = remember(embed, isMuted) { embed.inlinePlayerUrl(startMuted = isMuted) }

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    LaunchedEffect(webView, isPlaying, isMuted) {
        if (embed.supportsSoundControl) {
            webView?.sendYouTubeCommand(if (isPlaying) "playVideo" else "pauseVideo")
            webView?.sendYouTubeCommand(if (isMuted) "mute" else "unMute")
        }
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
                view.tag = url
                view.loadEmbedHtml(inlinePlayerHtml(url))
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

private fun VideoEmbed.inlinePlayerUrl(startMuted: Boolean): String {
    val baseUrl = playerUrl(autoplay = true)
    return when (provider) {
        VideoEmbed.Provider.YOUTUBE ->
            "$baseUrl&mute=${if (startMuted) 1 else 0}&loop=1&playlist=$id&enablejsapi=1" +
                "&controls=0&modestbranding=1&iv_load_policy=3&fs=0&disablekb=1"
        VideoEmbed.Provider.TIKTOK -> "$baseUrl&loop=1$TIKTOK_HIDDEN_CONTROLS"
        VideoEmbed.Provider.TWITCH -> baseUrl
    }
}

private fun WebView.sendYouTubeCommand(command: String) {
    evaluateJavascript(
        """
        (function() {
            var frame = document.querySelector('iframe');
            if (frame) {
                frame.contentWindow.postMessage('{"event":"command","func":"$command","args":[]}', '*');
            }
        })();
        """.trimIndent(),
        null,
    )
}

private fun inlinePlayerHtml(url: String): String = """
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
        src="${url.escapeHtmlAttribute()}"
        allow="autoplay; encrypted-media; fullscreen; picture-in-picture"
        referrerpolicy="strict-origin-when-cross-origin"
        allowfullscreen>
    </iframe>
    </body>
    </html>
""".trimIndent()
