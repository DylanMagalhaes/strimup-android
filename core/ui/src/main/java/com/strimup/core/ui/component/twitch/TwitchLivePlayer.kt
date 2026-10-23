package com.strimup.core.ui.component.twitch

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.strimup.core.streamer.domain.entity.EMBED_PARENT_DOMAIN
import com.strimup.core.ui.component.embed.configureForEmbed
import com.strimup.core.ui.component.embed.loadEmbedHtml

private const val PLAYER_WIDTH = 534
private const val PLAYER_HEIGHT = 300

@Composable
fun TwitchLivePlayer(
    channel: String,
    modifier: Modifier = Modifier,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(PLAYER_WIDTH.toFloat() / PLAYER_HEIGHT)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                configureForEmbed()
                webView = this
            }
        },
        update = { view ->
            if (view.tag != channel) {
                view.tag = channel
                view.loadEmbedHtml(twitchEmbedHtml(channel))
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

private fun twitchEmbedHtml(channel: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=$PLAYER_WIDTH, user-scalable=no">
    <style>
    html, body { margin: 0; padding: 0; background: #000; overflow: hidden; }
    iframe { display: block; border: 0; width: ${PLAYER_WIDTH}px; height: ${PLAYER_HEIGHT}px; }
    </style>
    </head>
    <body>
    <iframe
        src="https://player.twitch.tv/?channel=$channel&parent=$EMBED_PARENT_DOMAIN&muted=true&autoplay=true"
        allow="autoplay; fullscreen"
        allowfullscreen>
    </iframe>
    </body>
    </html>
""".trimIndent()
