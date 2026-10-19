package com.strimup.core.ui.component.twitch

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect

private const val TWITCH_EMBED_PARENT = "strimup.com"
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
                configureForTwitchEmbed()
                webView = this
            }
        },
        update = { view ->
            if (view.tag != channel) {
                view.tag = channel
                view.loadDataWithBaseURL(
                    "https://$TWITCH_EMBED_PARENT/",
                    twitchEmbedHtml(channel),
                    "text/html",
                    "utf-8",
                    null,
                )
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.configureForTwitchEmbed() {
    setBackgroundColor(Color.Black.toArgb())
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        useWideViewPort = true
        loadWithOverviewMode = true
        setSupportZoom(false)
    }
    webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
            request.isForMainFrame
    }
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
        src="https://player.twitch.tv/?channel=$channel&parent=$TWITCH_EMBED_PARENT&muted=true&autoplay=true"
        allow="autoplay; fullscreen"
        allowfullscreen>
    </iframe>
    </body>
    </html>
""".trimIndent()
