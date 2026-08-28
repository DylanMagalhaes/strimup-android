package com.strimup.core.ui.component.streamer

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerScreen(
    videoId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val window = remember(view) { (view.context as? ComponentActivity)?.window }

    DisposableEffect(window, view) {
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }

        window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
        controller?.apply {
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    val webView = remember(context) {
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
    }

    DisposableEffect(videoId, isVertical) {
        webView.loadDataWithBaseURL(
            SITE_URL,
            buildPlayerHtml(videoId = videoId, isVertical = isVertical),
            "text/html",
            "utf-8",
            null,
        )
        onDispose { }
    }

    // Lifecycle Observer pour la WebView
    DisposableEffect(lifecycleOwner, webView) {
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
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.destroy()
        }
    }

    BackHandler(onBack = onBack)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .displayCutoutPadding()
                .padding(top = 16.dp, end = 16.dp)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fermer",
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

private const val SITE_URL = "https://strimup.com"

private fun buildPlayerHtml(videoId: String, isVertical: Boolean): String {
    val safeId = videoId.filter { it.isLetterOrDigit() || it == '_' || it == '-' }

    val playerCss = if (isVertical) {
        """
        #player {
            position: absolute;
            top: 0;
            left: 50%;
            height: 100vh;
            width: calc(100vh * 16 / 9);
            transform: translateX(-50%);
        }
        """.trimIndent()
    } else {
        """
        #player {
            position: absolute;
            top: 50%;
            left: 0;
            width: 100vw;
            height: calc(100vw * 9 / 16);
            transform: translateY(-50%);
        }
        """.trimIndent()
    }

    val controls = if (isVertical) 0 else 1
    val tapDisplay = if (isVertical) "block" else "none"

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
        <style>
            html, body {
                margin: 0;
                padding: 0;
                width: 100%;
                height: 100%;
                background: #000;
                overflow: hidden;
            }
            #stage {
                position: fixed;
                inset: 0;
                background: #000;
                overflow: hidden;
            }
            $playerCss
            #tap {
                position: fixed;
                inset: 0;
                z-index: 2;
                background: transparent;
                display: $tapDisplay;
            }
        </style>
    </head>
    <body>
        <div id="stage"><div id="player"></div></div>
        <div id="tap"></div>
        <script src="https://www.youtube.com/iframe_api"></script>
        <script>
            var player;
            function onYouTubeIframeAPIReady() {
                player = new YT.Player('player', {
                    videoId: '$safeId',
                    playerVars: {
                        autoplay: 1,
                        controls: $controls,
                        playsinline: 1,
                        rel: 0,
                        modestbranding: 1,
                        fs: 0,
                        iv_load_policy: 3,
                        origin: '$SITE_URL'
                    },
                    events: {
                        onReady: function (e) { e.target.playVideo(); }
                    }
                });
            }
            document.getElementById('tap').addEventListener('click', function () {
                if (!player || !player.getPlayerState) return;
                var s = player.getPlayerState();
                if (s === YT.PlayerState.PLAYING) player.pauseVideo();
                else player.playVideo();
            });
        </script>
    </body>
    </html>
    """.trimIndent()
}