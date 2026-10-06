package com.strimup.core.ui.component.player

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class YouTubePlayerHost(val webView: WebView, val state: YouTubePlayerState)

@Composable
fun rememberYouTubePlayerHost(videoId: String, isVertical: Boolean): YouTubePlayerHost {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val host = remember(context) {
        val webView = createPlayerWebView(context)
        val state = YouTubePlayerState(WebViewYouTubePlayerCommands(webView))
        webView.addJavascriptInterface(YouTubePlayerBridge(state), YouTubePlayerBridge.NAME)
        YouTubePlayerHost(webView, state)
    }

    LaunchedEffect(host, videoId, isVertical) {
        host.webView.loadDataWithBaseURL(
            YOUTUBE_PLAYER_ORIGIN,
            buildYouTubePlayerHtml(videoId = videoId, isVertical = isVertical),
            "text/html",
            "utf-8",
            null,
        )
    }

    DisposableEffect(lifecycleOwner, host) {
        val webView = host.webView
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView.loadUrl("about:blank")
            webView.stopLoading()
            webView.removeJavascriptInterface(YouTubePlayerBridge.NAME)
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.destroy()
        }
    }

    return host
}

@SuppressLint("SetJavaScriptEnabled")
private fun createPlayerWebView(context: Context): WebView =
    WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        setBackgroundColor(Color.Black.toArgb())

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webChromeClient = WebChromeClient()
        webViewClient = WebViewClient()
    }
